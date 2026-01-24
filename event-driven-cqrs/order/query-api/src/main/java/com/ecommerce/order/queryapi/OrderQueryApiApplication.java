package com.ecommerce.order.queryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.order.queryapi",
        "com.ecommerce.shared.persistence"
})
public class OrderQueryApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderQueryApiApplication.class, args);
    }
}
