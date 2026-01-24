package com.ecommerce.inventory.commandhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.inventory.commandhandler",
        "com.ecommerce.inventory.application",
        "com.ecommerce.inventory.infrastructure"
})
public class InventoryCommandHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryCommandHandlerApplication.class, args);
    }
}
