package com.ecommerce.inventory.eventhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.inventory.eventhandler",
        "com.ecommerce.shared.persistence"
})
public class InventoryEventHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryEventHandlerApplication.class, args);
    }
}
