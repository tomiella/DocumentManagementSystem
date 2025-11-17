package at.bif.swen.ocrworker.config;

import at.bif.swen.ocrworker.messaging.DocumentCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitSerializationConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        Jackson2JsonMessageConverter conv = new Jackson2JsonMessageConverter(om);
        conv.setClassMapper(classMapper());
        return conv;
    }

    @Bean
    public ClassMapper classMapper() {
        DefaultClassMapper m = new DefaultClassMapper();
        m.setTrustedPackages("*");
        m.setIdClassMapping(Map.of(
                "at.bif.swen.rest.messaging.DocumentCreatedEvent", DocumentCreatedEvent.class // producer -> consumer type
        ));
        return m;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            org.springframework.amqp.rabbit.connection.ConnectionFactory cf,
            Jackson2JsonMessageConverter conv) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(conv);
        return f;
    }
}
