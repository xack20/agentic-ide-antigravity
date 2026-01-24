package com.ecommerce.inventory.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class StockSet implements DomainEvent {
    private final UUID eventId;
    private final String productId;
    private final int oldQty;
    private final int newQty;
    private final String reason;
    private final Instant occurredAt;

    public StockSet(String productId, int oldQty, int newQty, String reason) {
        this.eventId = UUID.randomUUID();
        this.productId = productId;
        this.oldQty = oldQty;
        this.newQty = newQty;
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
        return "StockSet";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
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

    public String getReason() {
        return reason;
    }
}
