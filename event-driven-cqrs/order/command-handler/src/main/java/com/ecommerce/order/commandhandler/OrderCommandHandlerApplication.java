package com.ecommerce.order.commandhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.order.commandhandler",
        "com.ecommerce.order.application",
        "com.ecommerce.order.infrastructure"
})
public class OrderCommandHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderCommandHandlerApplication.class, args);
    }
}
