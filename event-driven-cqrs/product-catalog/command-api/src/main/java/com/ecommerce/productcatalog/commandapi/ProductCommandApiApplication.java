package com.ecommerce.productcatalog.commandapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProductCatalog Command API Application.
 * Exposes REST endpoints for product commands.
 */
@SpringBootApplication(scanBasePackages = "com.ecommerce.productcatalog")
public class ProductCommandApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCommandApiApplication.class, args);
    }
}
