package com.ecommerce.productcatalog.queryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProductCatalog Query API Application.
 * Exposes read-only REST endpoints for product queries.
 */
@SpringBootApplication(scanBasePackages = "com.ecommerce.productcatalog.queryapi")
public class ProductQueryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductQueryApiApplication.class, args);
    }
}
