package com.ecommerce.productcatalog.eventhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProductCatalog EventHandler Application.
 * Consumes domain events and updates read models (projections).
 */
@SpringBootApplication(scanBasePackages = "com.ecommerce.productcatalog.eventhandler")
public class ProductEventHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductEventHandlerApplication.class, args);
    }
}
