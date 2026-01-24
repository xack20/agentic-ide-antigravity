package com.ecommerce.productcatalog.domain.events;

import com.ecommerce.shared.common.domain.BaseDomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when an existing product is updated.
 */
public class ProductUpdated extends BaseDomainEvent {

    private static final String AGGREGATE_TYPE = "Product";

    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String currency;

    public ProductUpdated(String productId, String name, String description,
            BigDecimal price, String currency) {
        super(productId, AGGREGATE_TYPE);
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
    }

    // For deserialization
    public ProductUpdated(UUID eventId, Instant occurredAt, String productId,
            String name, String description, BigDecimal price, String currency) {
        super(eventId, occurredAt, productId, AGGREGATE_TYPE);
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
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
}
