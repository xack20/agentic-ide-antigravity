package com.ecommerce.cart.infrastructure.messaging;

import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class CartRabbitMQEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(CartRabbitMQEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public CartRabbitMQEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletableFuture<Void> publish(DomainEvent event) {
        return CompletableFuture.runAsync(() -> {
            try {
                String json = objectMapper.writeValueAsString(event);

                Message message = MessageBuilder
                        .withBody(json.getBytes())
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setHeader(MessagingConstants.HEADER_EVENT_TYPE, event.getEventType())
                        .setHeader(MessagingConstants.HEADER_AGGREGATE_TYPE, event.getAggregateType())
                        .setMessageId(event.getEventId().toString())
                        .build();

                String routingKey = "cart." + event.getEventType();

                rabbitTemplate.send(MessagingConstants.EVENTS_EXCHANGE, routingKey, message);

                logger.info("Published event: type={}, id={}", event.getEventType(), event.getAggregateId());
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize event", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> publishAll(List<DomainEvent> events) {
        return CompletableFuture.runAsync(() -> {
            for (DomainEvent event : events) {
                publish(event).join();
            }
        });
    }
}
