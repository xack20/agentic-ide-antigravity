package com.ecommerce.order.application.ports;

import java.util.concurrent.CompletableFuture;

public interface InventoryService {
    CompletableFuture<Boolean> checkStock(String productId, int qty);
}
