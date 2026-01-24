package com.ecommerce.inventory.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public class StockBatchValidated implements DomainEvent {
    private final UUID eventId;
    private final String orderId;
    private final boolean success;
    private final String failureReason; // Null if success
    private final Instant occurredAt;

    public StockBatchValidated(String orderId, boolean success, String failureReason) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.success = success;
        this.failureReason = failureReason;
        this.occurredAt = Instant.now();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getAggregateId() {
        return orderId;
    } // Inventory doesn't have a single agg root for this batch

    @Override
    public String getAggregateType() {
        return "InventoryBatch";
    }

    @Override
    public String getEventType() {
        return "StockBatchValidated";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
