package com.ecommerce.inventory.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class StockDeductedForOrder implements DomainEvent {
    private final UUID eventId;
    private final String orderId;
    private final String productId;
    private final int qty;
    private final int oldQty;
    private final int newQty;
    private final Instant occurredAt;

    public StockDeductedForOrder(String orderId, String productId, int qty, int oldQty, int newQty) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
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
        return productId;
    }

    @Override
    public String getAggregateType() {
        return "InventoryItem";
    }

    @Override
    public String getEventType() {
        return "StockDeductedForOrder";
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

    public int getQty() {
        return qty;
    }

    public int getOldQty() {
        return oldQty;
    }

    public int getNewQty() {
        return newQty;
    }
}
