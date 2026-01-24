package com.ecommerce.productcatalog.eventhandler.config;

import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for EventHandler process.
 */
@Configuration
public class EventHandlerConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public FanoutExchange eventsExchange() {
        return new FanoutExchange(MessagingConstants.EVENTS_EXCHANGE);
    }

    @Bean
    public Queue productCatalogEventsQueue() {
        return QueueBuilder.durable(MessagingConstants.PRODUCT_CATALOG_EVENTS_QUEUE)
                .withArgument("x-dead-letter-exchange", MessagingConstants.DEAD_LETTER_EXCHANGE)
                .build();
    }

    @Bean
    public Binding eventsBinding() {
        return BindingBuilder
                .bind(productCatalogEventsQueue())
                .to(eventsExchange());
    }
}
