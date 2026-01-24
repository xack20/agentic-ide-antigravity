package com.ecommerce.order.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class OrderStockCommitRequested implements DomainEvent {
    private final UUID eventId;
    private final String orderId;
    private final Map<String, Integer> items; // productId -> qty
    private final Instant occurredAt;

    public OrderStockCommitRequested(String orderId, Map<String, Integer> items) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.items = items;
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
        return "OrderStockCommitRequested";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public Map<String, Integer> getItems() {
        return items;
    }
}
