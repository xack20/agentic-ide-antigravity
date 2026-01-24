package com.ecommerce.checkout.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CheckoutRequested implements DomainEvent {
    private final UUID eventId;
    private final String orderId; // Pre-generating ID for tracking
    private final String guestToken;
    private final CustomerInfo customer;
    private final ShippingAddress address;
    private final String idempotencyKey;
    private final Instant occurredAt;

    public CheckoutRequested(String orderId, String guestToken, CustomerInfo customer, ShippingAddress address,
            String idempotencyKey) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.guestToken = guestToken;
        this.customer = customer;
        this.address = address;
        this.idempotencyKey = idempotencyKey;
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
        return "CheckoutRequested";
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

    public CustomerInfo getCustomer() {
        return customer;
    }

    public ShippingAddress getAddress() {
        return address;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
