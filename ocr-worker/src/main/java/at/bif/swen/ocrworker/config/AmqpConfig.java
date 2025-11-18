package at.bif.swen.ocrworker.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    public static final String EXCHANGE = "dms.documents.exchange";
    public static final String ROUTING_KEY = "documents.created";
    public static final String QUEUE = "dms.ocr.queue";

    @Bean TopicExchange documentExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean Queue ocrQueue() { return QueueBuilder.durable(QUEUE).build(); }
    @Bean Binding ocrBinding() { return BindingBuilder.bind(ocrQueue()).to(documentExchange()).with(ROUTING_KEY); }
}
