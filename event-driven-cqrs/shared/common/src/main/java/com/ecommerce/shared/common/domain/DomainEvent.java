package com.ecommerce.shared.common.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events.
 * Events represent facts that have occurred in the domain.
 */
public interface DomainEvent {

    /**
     * Unique identifier for this event instance.
     */
    UUID getEventId();

    /**
     * Type name of the event for serialization/routing.
     */
    String getEventType();

    /**
     * When the event occurred.
     */
    Instant getOccurredAt();

    /**
     * The aggregate ID that produced this event.
     */
    String getAggregateId();

    /**
     * The type of aggregate that produced this event.
     */
    String getAggregateType();
}
