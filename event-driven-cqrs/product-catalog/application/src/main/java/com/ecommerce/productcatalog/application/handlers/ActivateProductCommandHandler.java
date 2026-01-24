package com.ecommerce.productcatalog.application.handlers;

import com.ecommerce.productcatalog.application.commands.ActivateProductCommand;
import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.productcatalog.domain.valueobjects.ProductId;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for ActivateProductCommand.
 */
public class ActivateProductCommandHandler implements CommandHandler<ActivateProductCommand, Void> {

    private static final Logger logger = LoggerFactory.getLogger(ActivateProductCommandHandler.class);

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public ActivateProductCommandHandler(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(ActivateProductCommand command) {
        logger.info("Handling ActivateProductCommand: commandId={}, productId={}",
                command.getCommandId(), command.getProductId());

        return productRepository.findById(ProductId.of(command.getProductId()))
                .thenCompose(optProduct -> {
                    if (optProduct.isEmpty()) {
                        throw new RuntimeException("Product not found: " + command.getProductId());
                    }

                    var product = optProduct.get();
                    product.activate();

                    var eventsToPublish = product.getUncommittedEvents();

                    return productRepository.save(product)
                            .thenCompose(saved -> eventPublisher.publishAll(eventsToPublish));
                });
    }

    @Override
    public Class<ActivateProductCommand> getCommandType() {
        return ActivateProductCommand.class;
    }
}
