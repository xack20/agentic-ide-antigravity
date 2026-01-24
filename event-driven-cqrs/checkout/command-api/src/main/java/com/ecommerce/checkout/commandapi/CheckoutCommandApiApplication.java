package com.ecommerce.checkout.commandapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.ecommerce.checkout", "com.ecommerce.shared" })
public class CheckoutCommandApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckoutCommandApiApplication.class, args);
    }
}
