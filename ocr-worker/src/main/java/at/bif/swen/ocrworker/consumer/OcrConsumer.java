package at.bif.swen.ocrworker.consumer;

import at.bif.swen.ocrworker.config.AmqpConfig;
import at.bif.swen.ocrworker.messaging.DocumentCreatedEvent;
import at.bif.swen.ocrworker.messaging.OcrResultEvent;
import at.bif.swen.ocrworker.service.MinioService;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class OcrConsumer {
    private static final Logger log = LoggerFactory.getLogger(OcrConsumer.class);
    private final MinioService minioService;
    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;
    private final at.bif.swen.ocrworker.service.GenAiService genAiService;

    @RabbitListener(queues = AmqpConfig.QUEUE)
    public void onDocument(DocumentCreatedEvent evt) {
        log.info("OCR worker received: id={} filename={} contentType={} size={}",
                evt.id(), evt.filename(), evt.contentType(), evt.size());

        File tempFile = null;
        try {
            InputStream fileStream = minioService.getFile(evt.filename());

            String extension = org.springframework.util.StringUtils.getFilenameExtension(evt.filename());
            tempFile = File.createTempFile("ocr-", "." + (extension != null ? extension : "pdf"));
            java.nio.file.Files.copy(fileStream, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");

            String result = tesseract.doOCR(tempFile);

            log.info("OCR Result for {}: {}", evt.filename(), result);

            String summary = genAiService.summarize(result);
            log.info("Summary for document id={}: {}", evt.id(), summary);

            OcrResultEvent resultEvent = new OcrResultEvent(evt.id(), result, summary);
            rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.RESULT_ROUTING_KEY, resultEvent);
            log.info("Published OCR result for document id={}", evt.id());

        } catch (Exception e) {
            log.error("Error processing OCR for id={}", evt.id(), e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
