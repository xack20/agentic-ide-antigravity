package com.ecommerce.order.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class OrderCartClearRequested implements DomainEvent {
    private final UUID eventId;
    private final String orderId;
    private final String guestToken;
    private final Instant occurredAt;

    public OrderCartClearRequested(String orderId, String guestToken) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.guestToken = guestToken;
        this.occurredAt = Instant.now();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getAggregateId() {
        return orderId;
    }

    @Override
    public String getAggregateType() {
        return "Order";
    }

    @Override
    public String getEventType() {
        return "OrderCartClearRequested";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getGuestToken() {
        return guestToken;
    }
}
