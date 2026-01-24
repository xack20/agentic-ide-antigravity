package com.ecommerce.productcatalog.application.handlers;

import com.ecommerce.productcatalog.application.commands.UpdateProductCommand;
import com.ecommerce.productcatalog.application.commands.UpdateProductResult;
import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.productcatalog.domain.valueobjects.Money;
import com.ecommerce.productcatalog.domain.valueobjects.ProductId;
import com.ecommerce.productcatalog.domain.valueobjects.ProductName;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for UpdateProductCommand.
 */
public class UpdateProductCommandHandler implements CommandHandler<UpdateProductCommand, UpdateProductResult> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateProductCommandHandler.class);

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public UpdateProductCommandHandler(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<UpdateProductResult> handle(UpdateProductCommand command) {
        logger.info("Handling UpdateProductCommand: commandId={}, productId={}",
                command.getCommandId(), command.getProductId());

        ProductId productId = ProductId.of(command.getProductId());

        return productRepository.findById(productId)
                .thenCompose(optionalProduct -> {
                    if (optionalProduct.isEmpty()) {
                        return CompletableFuture.completedFuture(
                                UpdateProductResult.failure("Product not found: " + command.getProductId()));
                    }

                    var product = optionalProduct.get();

                    try {
                        ProductName name = ProductName.of(command.getName());
                        Money price = Money.of(command.getPrice(), command.getCurrency());

                        product.update(name, command.getDescription(), price);

                        return productRepository.save(product)
                                .thenCompose(saved -> eventPublisher.publishAll(saved.getUncommittedEvents())
                                        .thenApply(v -> {
                                            saved.clearUncommittedEvents();
                                            return UpdateProductResult.success();
                                        }));
                    } catch (Exception ex) {
                        return CompletableFuture.completedFuture(
                                UpdateProductResult.failure(ex.getMessage()));
                    }
                })
                .exceptionally(ex -> {
                    logger.error("Failed to update product: {}", ex.getMessage(), ex);
                    return UpdateProductResult.failure("Failed to update product: " + ex.getMessage());
                });
    }

    @Override
    public Class<UpdateProductCommand> getCommandType() {
        return UpdateProductCommand.class;
    }
}
