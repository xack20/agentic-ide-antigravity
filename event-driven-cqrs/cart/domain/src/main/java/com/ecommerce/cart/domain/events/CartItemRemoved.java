package com.ecommerce.cart.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CartItemRemoved implements DomainEvent {
    private final UUID eventId;
    private final String cartId;
    private final String productId;
    private final Instant occurredAt;

    public CartItemRemoved(String cartId, String productId) {
        this.eventId = UUID.randomUUID();
        this.cartId = cartId;
        this.productId = productId;
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
        return "CartItemRemoved";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getCartId() {
        return cartId;
    }

    public String getProductId() {
        return productId;
    }
}
