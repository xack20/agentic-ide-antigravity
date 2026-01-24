package com.ecommerce.productcatalog.domain.valueobjects;

/**
 * Enumeration of possible product statuses.
 */
public enum ProductStatus {

    /**
     * Product is in draft state, not yet published.
     */
    DRAFT,

    /**
     * Product is active and available for sale.
     */
    ACTIVE,

    /**
     * Product is inactive and not available for sale.
     */
    INACTIVE,

    /**
     * Product has been deleted (soft delete).
     */
    DELETED
}
