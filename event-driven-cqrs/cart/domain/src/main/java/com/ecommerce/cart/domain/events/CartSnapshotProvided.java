package com.ecommerce.cart.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class CartSnapshotProvided implements DomainEvent {
    private final UUID eventId;
    private final String orderId;
    private final String guestToken;
    private final Map<String, Integer> items;
    private final Instant occurredAt;

    public CartSnapshotProvided(String orderId, String guestToken, Map<String, Integer> items) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.guestToken = guestToken;
        this.items = items;
        this.occurredAt = Instant.now();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getAggregateId() {
        return guestToken;
    } // Using guestToken as Agg ID for Cart events

    @Override
    public String getAggregateType() {
        return "ShoppingCart";
    }

    @Override
    public String getEventType() {
        return "CartSnapshotProvided";
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

    public Map<String, Integer> getItems() {
        return items;
    }
}
