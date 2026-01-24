package com.ecommerce.cart.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CartItemQuantityUpdated implements DomainEvent {
    private final UUID eventId;
    private final String cartId;
    private final String productId;
    private final int oldQty;
    private final int newQty;
    private final Instant occurredAt;

    public CartItemQuantityUpdated(String cartId, String productId, int oldQty, int newQty) {
        this.eventId = UUID.randomUUID();
        this.cartId = cartId;
        this.productId = productId;
        this.oldQty = oldQty;
        this.newQty = newQty;
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
        return "CartItemQuantityUpdated";
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

    public int getOldQty() {
        return oldQty;
    }

    public int getNewQty() {
        return newQty;
    }
}
