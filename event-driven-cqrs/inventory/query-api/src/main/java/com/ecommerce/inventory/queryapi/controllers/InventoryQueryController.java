package com.ecommerce.inventory.queryapi.controllers;

import com.ecommerce.inventory.queryapi.models.StockAvailabilityView;
import com.ecommerce.inventory.queryapi.repositories.StockAvailabilityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryQueryController {

    private final StockAvailabilityRepository repository;

    public InventoryQueryController(StockAvailabilityRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<StockAvailabilityView> getStock(@PathVariable String productId) {
        return repository.findById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
