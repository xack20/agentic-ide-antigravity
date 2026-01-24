package com.ecommerce.productcatalog.infrastructure.messaging;

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

/**
 * RabbitMQ implementation of EventPublisher.
 * Publishes domain events to fanout exchange.
 */
@Component
public class RabbitMQEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletableFuture<Void> publish(DomainEvent event) {
        return CompletableFuture.runAsync(() -> {
            try {
                String payload = objectMapper.writeValueAsString(event);

                Message message = MessageBuilder
                        .withBody(payload.getBytes())
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setHeader(MessagingConstants.HEADER_EVENT_TYPE, event.getEventType())
                        .setHeader(MessagingConstants.HEADER_AGGREGATE_TYPE, event.getAggregateType())
                        .setMessageId(event.getEventId().toString())
                        .build();

                rabbitTemplate.send(MessagingConstants.EVENTS_EXCHANGE, "", message);

                logger.info("Published event: type={}, aggregateId={}",
                        event.getEventType(), event.getAggregateId());
            } catch (JsonProcessingException ex) {
                logger.error("Failed to serialize event: {}", ex.getMessage(), ex);
                throw new RuntimeException("Failed to serialize event", ex);
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
