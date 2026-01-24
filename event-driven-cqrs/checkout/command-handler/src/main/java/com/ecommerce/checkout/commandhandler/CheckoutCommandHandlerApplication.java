package com.ecommerce.checkout.commandhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.ecommerce.checkout", "com.ecommerce.shared" })
public class CheckoutCommandHandlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckoutCommandHandlerApplication.class, args);
    }
}
