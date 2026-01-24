package com.ecommerce.productcatalog.domain.events;

import com.ecommerce.shared.common.domain.BaseDomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a product is deactivated.
 */
public class ProductDeactivated extends BaseDomainEvent {

    private static final String AGGREGATE_TYPE = "Product";

    public ProductDeactivated(String productId) {
        super(productId, AGGREGATE_TYPE);
    }

    // For deserialization
    public ProductDeactivated(UUID eventId, Instant occurredAt, String productId) {
        super(eventId, occurredAt, productId, AGGREGATE_TYPE);
    }

    public String getProductId() {
        return getAggregateId();
    }
}
