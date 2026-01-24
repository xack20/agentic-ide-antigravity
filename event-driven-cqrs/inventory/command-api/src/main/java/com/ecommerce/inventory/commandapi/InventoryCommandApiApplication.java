package com.ecommerce.inventory.commandapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.inventory.commandapi",
        "com.ecommerce.inventory.application", // access to commands if needed
        "com.ecommerce.shared.messaging" // access to CommandPublisher
})
public class InventoryCommandApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryCommandApiApplication.class, args);
    }
}
