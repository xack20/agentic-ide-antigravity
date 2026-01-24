package com.ecommerce.order.infrastructure.adapters;

import com.ecommerce.order.application.ports.CartService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class RestCartServiceAdapter implements CartService {

    private final RestTemplate restTemplate;

    public RestCartServiceAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getCartItems(String guestToken) {
        // In local setup, cart-query-api is at 8092
        String url = "http://localhost:8092/api/v1/carts/" + guestToken;

        return CompletableFuture.supplyAsync(() -> {
            try {
                // CartView response structure: { "items": { "prodId": qty, ... } }
                CartViewResponse response = restTemplate.getForObject(url, CartViewResponse.class);
                return response != null ? response.items : new HashMap<>();
            } catch (Exception e) {
                // Determine if 404 => empty cart
                return new HashMap<>();
            }
        });
    }

    record CartViewResponse(Map<String, Integer> items) {
    }
}
