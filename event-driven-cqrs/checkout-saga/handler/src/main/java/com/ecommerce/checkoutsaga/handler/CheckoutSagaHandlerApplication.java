package com.ecommerce.checkoutsaga.handler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.ecommerce.checkoutsaga", "com.ecommerce.shared" })
public class CheckoutSagaHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckoutSagaHandlerApplication.class, args);
    }
}
