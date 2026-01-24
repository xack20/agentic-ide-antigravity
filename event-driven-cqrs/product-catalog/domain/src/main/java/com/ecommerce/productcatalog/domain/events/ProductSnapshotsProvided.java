package com.ecommerce.productcatalog.domain.events;

import com.ecommerce.shared.common.domain.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProductSnapshotsProvided implements DomainEvent {
    private final UUID eventId;
    private final String orderId;
    private final List<ProductSnapshot> products;
    private final Instant occurredAt;

    public ProductSnapshotsProvided(String orderId, List<ProductSnapshot> products) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.products = products;
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
        return "ProductCatalog";
    } // Loose aggregate concept here

    @Override
    public String getEventType() {
        return "ProductSnapshotsProvided";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<ProductSnapshot> getProducts() {
        return products;
    }

    public record ProductSnapshot(String id, String name, String sku, BigDecimal price, boolean active) {
    }
}
