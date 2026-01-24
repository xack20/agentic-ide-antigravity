package com.ecommerce.order.commandapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.order.commandapi",
        "com.ecommerce.order.application",
        "com.ecommerce.shared.messaging"
})
public class OrderCommandApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderCommandApiApplication.class, args);
    }
}
