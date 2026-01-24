package com.ecommerce.shared.common.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Base implementation of DomainEvent providing common functionality.
 */
public abstract class BaseDomainEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;

    protected BaseDomainEvent(String aggregateId, String aggregateType) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }

    protected BaseDomainEvent(UUID eventId, Instant occurredAt, String aggregateId, String aggregateType) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getAggregateType() {
        return aggregateType;
    }
}
