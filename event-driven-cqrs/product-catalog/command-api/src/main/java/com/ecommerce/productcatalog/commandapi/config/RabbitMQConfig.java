package com.ecommerce.productcatalog.commandapi.config;

import com.ecommerce.shared.common.commands.Command;
import com.ecommerce.shared.common.commands.CommandEnvelope;
import com.ecommerce.shared.messaging.CommandPublisher;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;

/**
 * RabbitMQ configuration for Command API.
 */
@Configuration
public class RabbitMQConfig {

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
    public Binding productCatalogCommandsBinding() {
        return BindingBuilder
                .bind(productCatalogCommandsQueue())
                .to(commandsExchange())
                .with(MessagingConstants.PRODUCT_CATALOG_COMMANDS_QUEUE);
    }

    @Bean
    public CommandPublisher commandPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        return new CommandPublisher() {
            @Override
            public <T extends Command<?>> CompletableFuture<Void> publish(String queueName,
                    CommandEnvelope<T> envelope) {
                return CompletableFuture.runAsync(() -> {
                    rabbitTemplate.convertAndSend(
                            MessagingConstants.COMMANDS_EXCHANGE,
                            queueName,
                            envelope);
                });
            }
        };
    }
}
