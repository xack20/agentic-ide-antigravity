package com.ecommerce.productcatalog.application.ports;

import com.ecommerce.productcatalog.domain.aggregates.Product;
import com.ecommerce.productcatalog.domain.valueobjects.ProductId;
import com.ecommerce.shared.common.persistence.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for Product aggregate.
 * Defined in application layer, implemented in infrastructure.
 */
public interface ProductRepository extends Repository<Product, ProductId> {

    /**
     * Check if a SKU already exists.
     */
    CompletableFuture<Boolean> existsBySku(String sku);

    // Assuming save and findById are inherited from Repository,
    // but the instruction implies they might be explicitly declared or
    // the new method should be placed relative to them.
    // Given the instruction "Add findByIds method" and the snippet,
    // the most direct interpretation is to add the new method.
    // The snippet also implies the need for Optional and List imports.

    // The following methods are typically part of the Repository interface,
    // but are included here based on the structure implied by the user's snippet
    // if they were to be explicitly declared or if the snippet was meant to show
    // the full interface content.
    // However, since ProductRepository extends Repository, these are already
    // available.
    // I will only add the explicitly requested findByIds method.

    /**
     * Finds products by a list of IDs.
     */
    CompletableFuture<List<Product>> findByIds(List<String> ids);
}
