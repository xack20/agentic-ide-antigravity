package com.ecommerce.productcatalog.application.handlers;

import com.ecommerce.productcatalog.application.commands.UpdateProductDetailsCommand;
import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.productcatalog.domain.valueobjects.ProductDescription;
import com.ecommerce.productcatalog.domain.valueobjects.ProductId;
import com.ecommerce.productcatalog.domain.valueobjects.ProductName;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for UpdateProductDetailsCommand.
 */
public class UpdateProductDetailsCommandHandler implements CommandHandler<UpdateProductDetailsCommand, Void> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateProductDetailsCommandHandler.class);

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public UpdateProductDetailsCommandHandler(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(UpdateProductDetailsCommand command) {
        logger.info("Handling UpdateProductDetailsCommand: commandId={}, productId={}",
                command.getCommandId(), command.getProductId());

        return productRepository.findById(ProductId.of(command.getProductId()))
                .thenCompose(optProduct -> {
                    if (optProduct.isEmpty()) {
                        throw new RuntimeException("Product not found: " + command.getProductId());
                    }

                    var product = optProduct.get();
                    product.updateDetails(
                            ProductName.of(command.getName()),
                            ProductDescription.of(command.getDescription()));

                    var eventsToPublish = product.getUncommittedEvents();

                    return productRepository.save(product)
                            .thenCompose(saved -> eventPublisher.publishAll(eventsToPublish));
                });
    }

    @Override
    public Class<UpdateProductDetailsCommand> getCommandType() {
        return UpdateProductDetailsCommand.class;
    }
}
