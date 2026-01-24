package com.ecommerce.productcatalog.domain.events;

import com.ecommerce.shared.common.domain.BaseDomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a new product is created.
 */
public class ProductCreated extends BaseDomainEvent {

    private static final String AGGREGATE_TYPE = "Product";

    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String currency;
    private final String sku;
    private final String status;

    public ProductCreated(String productId, String name, String description,
            BigDecimal price, String currency, String sku, String status) {
        super(productId, AGGREGATE_TYPE);
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.sku = sku;
        this.status = status;
    }

    // For deserialization
    public ProductCreated(UUID eventId, Instant occurredAt, String productId,
            String name, String description, BigDecimal price,
            String currency, String sku, String status) {
        super(eventId, occurredAt, productId, AGGREGATE_TYPE);
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.sku = sku;
        this.status = status;
    }

    public String getProductId() {
        return getAggregateId();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getSku() {
        return sku;
    }

    public String getStatus() {
        return status;
    }
}
