package com.ecommerce.cart.commandapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.cart.commandapi",
        "com.ecommerce.cart.application", // Commands/DTOs/Logic might be needed if shared
        "com.ecommerce.shared.messaging" // CommandPublisher
})
public class CartCommandApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartCommandApiApplication.class, args);
    }
}
