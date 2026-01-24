package com.ecommerce.inventory.application.ports;

import com.ecommerce.inventory.domain.aggregates.InventoryItem;
import com.ecommerce.inventory.domain.valueobjects.ProductId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface InventoryRepository {
    CompletableFuture<Optional<InventoryItem>> findById(ProductId id);

    CompletableFuture<InventoryItem> save(InventoryItem inventoryItem);

    CompletableFuture<Boolean> exists(ProductId id);
}
