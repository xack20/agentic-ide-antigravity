package com.ecommerce.productcatalog.domain.events;

import com.ecommerce.shared.common.domain.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when product details (name, description) are updated.
 */
public class ProductDetailsUpdated extends BaseDomainEvent {

    private static final String AGGREGATE_TYPE = "Product";

    private final String name;
    private final String description;

    public ProductDetailsUpdated(String productId, String name, String description) {
        super(productId, AGGREGATE_TYPE);
        this.name = name;
        this.description = description;
    }

    // For deserialization
    public ProductDetailsUpdated(UUID eventId, Instant occurredAt, String productId,
            String name, String description) {
        super(eventId, occurredAt, productId, AGGREGATE_TYPE);
        this.name = name;
        this.description = description;
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
}
