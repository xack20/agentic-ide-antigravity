package com.ecommerce.cart.eventhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.cart.eventhandler",
        "com.ecommerce.cart.queryapi", // Reuse repositories/models from query-api
        "com.ecommerce.shared.persistence"
})
public class CartEventHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartEventHandlerApplication.class, args);
    }
}
