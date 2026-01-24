package com.ecommerce.productcatalog.commandhandler.config;

import com.ecommerce.productcatalog.application.handlers.CreateProductCommandHandler;
import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.shared.common.events.EventPublisher;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for CommandHandler process.
 */
@Configuration
public class CommandHandlerConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public FanoutExchange eventsExchange() {
        return new FanoutExchange(MessagingConstants.EVENTS_EXCHANGE);
    }

    @Bean
    public DirectExchange commandsExchange() {
        return new DirectExchange(MessagingConstants.COMMANDS_EXCHANGE);
    }

    @Bean
    public Queue productCatalogCommandsQueue() {
        return QueueBuilder.durable(MessagingConstants.PRODUCT_CATALOG_COMMANDS_QUEUE)
                .withArgument("x-dead-letter-exchange", MessagingConstants.DEAD_LETTER_EXCHANGE)
                .build();
    }

    @Bean
    public Binding commandsBinding() {
        return BindingBuilder
                .bind(productCatalogCommandsQueue())
                .to(commandsExchange())
                .with(MessagingConstants.PRODUCT_CATALOG_COMMANDS_QUEUE);
    }

    @Bean
    public CreateProductCommandHandler createProductCommandHandler(
            ProductRepository productRepository,
            EventPublisher eventPublisher) {
        return new CreateProductCommandHandler(productRepository, eventPublisher);
    }
}
