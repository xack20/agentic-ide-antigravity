package com.ecommerce.cart.queryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ecommerce.cart.queryapi",
        "com.ecommerce.shared.persistence"
})
public class CartQueryApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartQueryApiApplication.class, args);
    }
}
