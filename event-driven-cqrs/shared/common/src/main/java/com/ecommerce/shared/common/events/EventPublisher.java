package com.ecommerce.shared.common.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for publishing domain events to the message broker.
 */
public interface EventPublisher {

    /**
     * Publish a single event.
     *
     * @param event The event to publish
     * @return CompletableFuture that completes when publish is acknowledged
     */
    CompletableFuture<Void> publish(DomainEvent event);

    /**
     * Publish multiple events in order.
     *
     * @param events The events to publish
     * @return CompletableFuture that completes when all publishes are acknowledged
     */
    CompletableFuture<Void> publishAll(List<DomainEvent> events);
}
