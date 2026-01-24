package com.ecommerce.cart.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CartCreated implements DomainEvent {
    private final UUID eventId;
    private final String cartId;
    private final String guestToken;
    private final Instant occurredAt;

    public CartCreated(String cartId, String guestToken) {
        this.eventId = UUID.randomUUID();
        this.cartId = cartId;
        this.guestToken = guestToken;
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
        return "CartCreated";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getCartId() {
        return cartId;
    }

    public String getGuestToken() {
        return guestToken;
    }
}
