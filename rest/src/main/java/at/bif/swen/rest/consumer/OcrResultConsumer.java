package at.bif.swen.rest.consumer;

import at.bif.swen.rest.config.AmqpConfig;
import at.bif.swen.rest.messaging.OcrResultEvent;
import at.bif.swen.rest.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OcrResultConsumer {
    private static final Logger log = LoggerFactory.getLogger(OcrResultConsumer.class);
    private final DocumentRepository documentRepository;

    @RabbitListener(queues = AmqpConfig.RESULT_QUEUE)
    public void onOcrResult(OcrResultEvent evt) {
        UUID docId = evt.documentId();
        if (docId == null) {
            log.warn("Received OCR result with null document ID");
            return;
        }
        log.info("Received OCR result for document id={}", docId);

        documentRepository.findById(docId).ifPresentOrElse(
                document -> {
                    document.setOcrText(evt.ocrText());
                    documentRepository.save(document);
                    log.info("Updated document id={} with OCR text", docId);
                },
                () -> log.warn("Document id={} not found, cannot save OCR result", docId));
    }
}
