package com.ecommerce.productcatalog.commandhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * ProductCatalog CommandHandler Application.
 * Consumes commands from RabbitMQ queue and executes domain logic.
 */
@SpringBootApplication(scanBasePackages = "com.ecommerce.productcatalog")
@EnableMongoRepositories(basePackages = "com.ecommerce.productcatalog.infrastructure.persistence")
public class ProductCommandHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCommandHandlerApplication.class, args);
    }
}
