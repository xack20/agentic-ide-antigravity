package com.ecommerce.inventory.queryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.inventory.queryapi",
        "com.ecommerce.shared.persistence" // if generic tools used
})
public class InventoryQueryApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryQueryApiApplication.class, args);
    }
}
