package com.ecommerce.productcatalog.application.ports;

import com.ecommerce.productcatalog.domain.aggregates.Product;
import com.ecommerce.productcatalog.domain.valueobjects.ProductId;
import com.ecommerce.shared.common.persistence.Repository;

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
}
