package com.ecommerce.order.infrastructure.adapters;

import com.ecommerce.order.application.ports.InventoryService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Component
public class RestInventoryServiceAdapter implements InventoryService {

    private final RestTemplate restTemplate;

    public RestInventoryServiceAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CompletableFuture<Boolean> checkStock(String productId, int qty) {
        // Inventory Query API at 8084 (implied, step 1224: query-api RUNNING)
        // Actually Inventory query API is 8084.

        String url = "http://localhost:8084/api/v1/stock/" + productId;

        return CompletableFuture.supplyAsync(() -> {
            try {
                // StockView: { productId, quantityAvailable }
                StockViewResponse response = restTemplate.getForObject(url, StockViewResponse.class);
                return response != null && response.quantityAvailable() >= qty;
            } catch (Exception e) {
                return false;
            }
        });
    }

    record StockViewResponse(String productId, int quantityAvailable) {
    }
}
