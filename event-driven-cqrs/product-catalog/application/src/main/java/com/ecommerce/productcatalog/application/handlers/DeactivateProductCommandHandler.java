package com.ecommerce.productcatalog.application.handlers;

import com.ecommerce.productcatalog.application.commands.DeactivateProductCommand;
import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.productcatalog.domain.valueobjects.ProductId;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for DeactivateProductCommand.
 */
public class DeactivateProductCommandHandler implements CommandHandler<DeactivateProductCommand, Void> {

    private static final Logger logger = LoggerFactory.getLogger(DeactivateProductCommandHandler.class);

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public DeactivateProductCommandHandler(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(DeactivateProductCommand command) {
        logger.info("Handling DeactivateProductCommand: commandId={}, productId={}",
                command.getCommandId(), command.getProductId());

        return productRepository.findById(ProductId.of(command.getProductId()))
                .thenCompose(optProduct -> {
                    if (optProduct.isEmpty()) {
                        throw new RuntimeException("Product not found: " + command.getProductId());
                    }

                    var product = optProduct.get();
                    product.deactivate();

                    var eventsToPublish = product.getUncommittedEvents();

                    return productRepository.save(product)
                            .thenCompose(saved -> eventPublisher.publishAll(eventsToPublish));
                });
    }

    @Override
    public Class<DeactivateProductCommand> getCommandType() {
        return DeactivateProductCommand.class;
    }
}
