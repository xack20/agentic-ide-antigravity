package com.ecommerce.productcatalog.commandhandler.config;

import com.ecommerce.productcatalog.application.handlers.CreateProductCommandHandler;
import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.shared.common.events.EventPublisher;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new SimpleMessageConverter());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
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
