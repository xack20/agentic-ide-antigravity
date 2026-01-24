package com.ecommerce.order.eventhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.order.eventhandler",
        "com.ecommerce.order.queryapi", // Reuse read models
        "com.ecommerce.shared.persistence"
})
public class OrderEventHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderEventHandlerApplication.class, args);
    }
}
