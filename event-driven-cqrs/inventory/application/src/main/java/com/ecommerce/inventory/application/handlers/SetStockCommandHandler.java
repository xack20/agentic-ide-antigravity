package com.ecommerce.inventory.application.handlers;

import com.ecommerce.inventory.application.commands.SetStockCommand;
import com.ecommerce.inventory.application.ports.InventoryRepository;
import com.ecommerce.inventory.domain.aggregates.InventoryItem;
import com.ecommerce.inventory.domain.valueobjects.AdjustmentReason;
import com.ecommerce.inventory.domain.valueobjects.ProductId;
import com.ecommerce.inventory.domain.valueobjects.Quantity;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SetStockCommandHandler implements CommandHandler<SetStockCommand, Void> {

    private final InventoryRepository repository;
    private final EventPublisher eventPublisher;

    public SetStockCommandHandler(InventoryRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(SetStockCommand command) {
        ProductId productId = ProductId.of(command.getProductId());

        return repository.findById(productId)
                .thenCompose(optItem -> {
                    InventoryItem item;
                    if (optItem.isPresent()) {
                        item = optItem.get();
                    } else {
                        item = InventoryItem.create(productId);
                    }

                    item.setStock(
                            Quantity.of(command.getNewQty()),
                            command.getReason() != null ? AdjustmentReason.of(command.getReason()) : null);

                    return repository.save(item);
                })
                .thenAccept(saved -> {
                    List<DomainEvent> events = saved.getUncommittedEvents();
                    for (DomainEvent event : events) {
                        eventPublisher.publish(event);
                    }
                    saved.clearUncommittedEvents();
                });
    }

    @Override
    public Class<SetStockCommand> getCommandType() {
        return SetStockCommand.class;
    }
}
