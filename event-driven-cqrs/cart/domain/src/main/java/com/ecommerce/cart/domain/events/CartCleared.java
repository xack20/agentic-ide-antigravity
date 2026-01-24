package com.ecommerce.cart.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CartCleared implements DomainEvent {
    private final UUID eventId;
    private final String cartId;
    private final String orderId;
    private final Instant occurredAt;

    public CartCleared(String cartId, String orderId) {
        this.eventId = UUID.randomUUID();
        this.cartId = cartId;
        this.orderId = orderId;
        this.occurredAt = Instant.now();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getAggregateId() {
        return cartId;
    }

    @Override
    public String getAggregateType() {
        return "ShoppingCart";
    }

    @Override
    public String getEventType() {
        return "CartCleared";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getCartId() {
        return cartId;
    }

    public String getOrderId() {
        return orderId;
    }
}
