package at.bif.swen.ocrworker.consumer;

import at.bif.swen.ocrworker.config.AmqpConfig;
import at.bif.swen.ocrworker.messaging.DocumentCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OcrConsumer {
    private static final Logger log = LoggerFactory.getLogger(OcrConsumer.class);

    @RabbitListener(queues = AmqpConfig.QUEUE)
    public void onDocument(DocumentCreatedEvent evt) {
        log.info("OCR demo received: id={} filename={} contentType={} size={}",
                evt.id(), evt.filename(), evt.contentType(), evt.size());
        // TODO: ocr
    }
}
