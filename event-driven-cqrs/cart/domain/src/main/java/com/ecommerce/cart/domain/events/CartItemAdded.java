package com.ecommerce.cart.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CartItemAdded implements DomainEvent {
    private final UUID eventId;
    private final String cartId;
    private final String productId;
    private final int qty;
    private final Instant occurredAt;

    public CartItemAdded(String cartId, String productId, int qty) {
        this.eventId = UUID.randomUUID();
        this.cartId = cartId;
        this.productId = productId;
        this.qty = qty;
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
        return "CartItemAdded";
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

    public int getQty() {
        return qty;
    }
}
