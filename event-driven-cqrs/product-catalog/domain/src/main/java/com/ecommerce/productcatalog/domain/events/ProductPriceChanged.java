package com.ecommerce.productcatalog.domain.events;

import com.ecommerce.shared.common.domain.BaseDomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when product price is changed.
 */
public class ProductPriceChanged extends BaseDomainEvent {

    private static final String AGGREGATE_TYPE = "Product";

    private final BigDecimal oldPrice;
    private final BigDecimal newPrice;
    private final String currency;

    public ProductPriceChanged(String productId, BigDecimal oldPrice, BigDecimal newPrice, String currency) {
        super(productId, AGGREGATE_TYPE);
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.currency = currency;
    }

    // For deserialization
    public ProductPriceChanged(UUID eventId, Instant occurredAt, String productId,
            BigDecimal oldPrice, BigDecimal newPrice, String currency) {
        super(eventId, occurredAt, productId, AGGREGATE_TYPE);
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.currency = currency;
    }

    public String getProductId() {
        return getAggregateId();
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public String getCurrency() {
        return currency;
    }
}
