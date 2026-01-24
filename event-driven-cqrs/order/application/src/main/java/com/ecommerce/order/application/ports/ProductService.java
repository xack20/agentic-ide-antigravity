package com.ecommerce.order.application.ports;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    CompletableFuture<ProductDto> getProduct(String productId);

    record ProductDto(String id, String name, String sku, BigDecimal price, boolean active) {
    }
}
