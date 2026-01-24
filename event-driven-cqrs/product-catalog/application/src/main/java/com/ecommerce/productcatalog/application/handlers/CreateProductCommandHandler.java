package com.ecommerce.productcatalog.application.handlers;

import com.ecommerce.productcatalog.application.commands.CreateProductCommand;
import com.ecommerce.productcatalog.application.commands.CreateProductResult;
import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.productcatalog.domain.aggregates.Product;
import com.ecommerce.productcatalog.domain.valueobjects.Money;
import com.ecommerce.productcatalog.domain.valueobjects.ProductId;
import com.ecommerce.productcatalog.domain.valueobjects.ProductName;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for CreateProductCommand.
 * Orchestrates product creation following DDD patterns.
 */
public class CreateProductCommandHandler implements CommandHandler<CreateProductCommand, CreateProductResult> {

    private static final Logger logger = LoggerFactory.getLogger(CreateProductCommandHandler.class);

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public CreateProductCommandHandler(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<CreateProductResult> handle(CreateProductCommand command) {
        logger.info("Handling CreateProductCommand: commandId={}, sku={}",
                command.getCommandId(), command.getSku());

        return productRepository.existsBySku(command.getSku())
                .thenCompose(exists -> {
                    if (exists) {
                        logger.warn("Product with SKU {} already exists", command.getSku());
                        return CompletableFuture.completedFuture(
                                CreateProductResult
                                        .failure("Product with SKU " + command.getSku() + " already exists"));
                    }

                    return createProduct(command);
                })
                .exceptionally(ex -> {
                    logger.error("Failed to create product: {}", ex.getMessage(), ex);
                    return CreateProductResult.failure("Failed to create product: " + ex.getMessage());
                });
    }

    private CompletableFuture<CreateProductResult> createProduct(CreateProductCommand command) {
        try {
            // Create aggregate using factory method
            ProductId productId = ProductId.generate();
            ProductName name = ProductName.of(command.getName());
            Money price = Money.of(command.getPrice(), command.getCurrency());

            Product product = Product.create(
                    productId,
                    name,
                    command.getDescription(),
                    price,
                    command.getSku());

            // Capture uncommitted events BEFORE save (repository reconstitutes without
            // events)
            var eventsToPublish = product.getUncommittedEvents();
            String createdProductId = product.getId().getValue();

            // Save aggregate (writes state to DB)
            return productRepository.save(product)
                    .thenCompose(savedProduct -> {
                        // Publish events from the original aggregate
                        return eventPublisher.publishAll(eventsToPublish)
                                .thenApply(v -> {
                                    logger.info("Product created successfully: productId={}",
                                            createdProductId);
                                    return CreateProductResult.success(createdProductId);
                                });
                    });
        } catch (IllegalArgumentException ex) {
            logger.warn("Validation failed for CreateProductCommand: {}", ex.getMessage());
            return CompletableFuture.completedFuture(CreateProductResult.failure(ex.getMessage()));
        }
    }

    @Override
    public Class<CreateProductCommand> getCommandType() {
        return CreateProductCommand.class;
    }
}
