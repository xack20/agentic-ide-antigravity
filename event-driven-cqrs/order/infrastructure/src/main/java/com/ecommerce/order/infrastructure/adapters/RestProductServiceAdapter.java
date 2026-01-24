package com.ecommerce.order.infrastructure.adapters;

import com.ecommerce.order.application.ports.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Component
public class RestProductServiceAdapter implements ProductService {

    private final RestTemplate restTemplate;

    public RestProductServiceAdapter() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CompletableFuture<ProductDto> getProduct(String productId) {
        // Product Query API at 8083 or Command Handler at 8080?
        // Query API port 8083 (from manage-services.sh history, Step 972/1075 contexts)
        // Wait, manage-services.sh says:
        // product-catalog:query-api -> 8083

        String url = "http://localhost:8083/api/v1/products/" + productId;

        return CompletableFuture.supplyAsync(() -> {
            try {
                // ProductView response: { id, name, sku, price, active }
                ProductViewResponse response = restTemplate.getForObject(url, ProductViewResponse.class);
                if (response == null)
                    throw new RuntimeException("Product not found");

                return new ProductDto(
                        response.id(),
                        response.name(),
                        response.sku(),
                        response.price(),
                        response.active());
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch product " + productId, e);
            }
        });
    }

    record ProductViewResponse(String id, String name, String sku, BigDecimal price, boolean active) {
    }
}
