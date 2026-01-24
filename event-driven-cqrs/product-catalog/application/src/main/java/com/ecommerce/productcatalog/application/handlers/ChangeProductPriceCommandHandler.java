package com.ecommerce.productcatalog.application.handlers;

import com.ecommerce.productcatalog.application.commands.ChangeProductPriceCommand;
import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.productcatalog.domain.valueobjects.Money;
import com.ecommerce.productcatalog.domain.valueobjects.ProductId;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for ChangeProductPriceCommand.
 */
public class ChangeProductPriceCommandHandler implements CommandHandler<ChangeProductPriceCommand, Void> {

    private static final Logger logger = LoggerFactory.getLogger(ChangeProductPriceCommandHandler.class);

    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public ChangeProductPriceCommandHandler(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(ChangeProductPriceCommand command) {
        logger.info("Handling ChangeProductPriceCommand: commandId={}, productId={}",
                command.getCommandId(), command.getProductId());

        return productRepository.findById(ProductId.of(command.getProductId()))
                .thenCompose(optProduct -> {
                    if (optProduct.isEmpty()) {
                        throw new RuntimeException("Product not found: " + command.getProductId());
                    }

                    var product = optProduct.get();
                    product.changePrice(Money.of(command.getNewPrice(), command.getCurrency()));

                    var eventsToPublish = product.getUncommittedEvents();

                    return productRepository.save(product)
                            .thenCompose(saved -> eventPublisher.publishAll(eventsToPublish));
                });
    }

    @Override
    public Class<ChangeProductPriceCommand> getCommandType() {
        return ChangeProductPriceCommand.class;
    }
}
