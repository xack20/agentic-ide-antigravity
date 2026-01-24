package com.ecommerce.shared.common.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Envelope wrapping a domain event with cross-cutting metadata.
 * Used for correlation, tracing, and routing.
 */
public class EventEnvelope {

    private final String eventId;
    private final String eventType;
    private final String aggregateId;
    private final String aggregateType;
    private final int aggregateVersion;
    private final String correlationId;
    private final String causationId;
    private final String tenantId;
    private final Instant timestamp;
    private final String payload;

    private EventEnvelope(Builder builder) {
        this.eventId = builder.eventId;
        this.eventType = builder.eventType;
        this.aggregateId = builder.aggregateId;
        this.aggregateType = builder.aggregateType;
        this.aggregateVersion = builder.aggregateVersion;
        this.correlationId = builder.correlationId;
        this.causationId = builder.causationId;
        this.tenantId = builder.tenantId;
        this.timestamp = builder.timestamp;
        this.payload = builder.payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder fromEvent(DomainEvent event) {
        return new Builder()
                .eventId(event.getEventId().toString())
                .eventType(event.getEventType())
                .aggregateId(event.getAggregateId())
                .aggregateType(event.getAggregateType())
                .timestamp(event.getOccurredAt());
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public int getAggregateVersion() {
        return aggregateVersion;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getCausationId() {
        return causationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getPayload() {
        return payload;
    }

    public static class Builder {
        private String eventId = UUID.randomUUID().toString();
        private String eventType;
        private String aggregateId;
        private String aggregateType;
        private int aggregateVersion;
        private String correlationId;
        private String causationId;
        private String tenantId;
        private Instant timestamp = Instant.now();
        private String payload;

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public Builder aggregateType(String aggregateType) {
            this.aggregateType = aggregateType;
            return this;
        }

        public Builder aggregateVersion(int aggregateVersion) {
            this.aggregateVersion = aggregateVersion;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder causationId(String causationId) {
            this.causationId = causationId;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public EventEnvelope build() {
            return new EventEnvelope(this);
        }
    }
}
