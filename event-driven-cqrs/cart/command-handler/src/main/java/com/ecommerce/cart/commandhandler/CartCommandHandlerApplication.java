package com.ecommerce.cart.commandhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.cart.commandhandler",
        "com.ecommerce.cart.application",
        "com.ecommerce.cart.infrastructure"
})
public class CartCommandHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartCommandHandlerApplication.class, args);
    }
}
