package at.bif.swen.rest.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    public static final String EXCHANGE = "dms.documents.exchange";
    public static final String ROUTING_KEY = "documents.created";
    public static final String QUEUE = "dms.ocr.queue";

    public static final String RESULT_ROUTING_KEY = "ocr.result";
    public static final String RESULT_QUEUE = "dms.ocr.result.queue";

    @Bean
    TopicExchange documentExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue ocrQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    Binding ocrBinding() {
        return BindingBuilder.bind(ocrQueue()).to(documentExchange()).with(ROUTING_KEY);
    }

    @Bean
    Queue ocrResultQueue() {
        return QueueBuilder.durable(RESULT_QUEUE).build();
    }

    @Bean
    Binding ocrResultBinding() {
        return BindingBuilder.bind(ocrResultQueue()).to(documentExchange()).with(RESULT_ROUTING_KEY);
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter(com.fasterxml.jackson.databind.ObjectMapper om) {
        return new Jackson2JsonMessageConverter(om);
    }
}
