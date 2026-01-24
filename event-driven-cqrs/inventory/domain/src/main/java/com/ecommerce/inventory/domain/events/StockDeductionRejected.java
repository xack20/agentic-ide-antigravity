package com.ecommerce.inventory.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class StockDeductionRejected implements DomainEvent {
    private final UUID eventId;
    private final String orderId;
    private final String productId;
    private final int requestedQty;
    private final int availableQty;
    private final String reason;
    private final Instant occurredAt;

    public StockDeductionRejected(String orderId, String productId, int requestedQty, int availableQty, String reason) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.productId = productId;
        this.requestedQty = requestedQty;
        this.availableQty = availableQty;
        this.reason = reason;
        this.occurredAt = Instant.now();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getAggregateId() {
        return productId;
    }

    @Override
    public String getAggregateType() {
        return "InventoryItem";
    }

    @Override
    public String getEventType() {
        return "StockDeductionRejected";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getRequestedQty() {
        return requestedQty;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public String getReason() {
        return reason;
    }
}
