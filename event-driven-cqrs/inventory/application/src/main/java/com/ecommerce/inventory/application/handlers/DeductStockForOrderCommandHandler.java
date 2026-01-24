package com.ecommerce.inventory.application.handlers;

import com.ecommerce.inventory.application.commands.DeductStockForOrderCommand;
import com.ecommerce.inventory.application.ports.InventoryRepository;
import com.ecommerce.inventory.domain.aggregates.InventoryItem;
import com.ecommerce.inventory.domain.valueobjects.ProductId;
import com.ecommerce.inventory.domain.valueobjects.Quantity;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DeductStockForOrderCommandHandler implements CommandHandler<DeductStockForOrderCommand, Void> {

    private final InventoryRepository repository;
    private final EventPublisher eventPublisher;

    public DeductStockForOrderCommandHandler(InventoryRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(DeductStockForOrderCommand command) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (DeductStockForOrderCommand.OrderItem orderItem : command.getItems()) {
            ProductId pid = ProductId.of(orderItem.getProductId());

            CompletableFuture<Void> deduction = repository.findById(pid)
                    .thenCompose(optItem -> {
                        if (optItem.isPresent()) {
                            InventoryItem item = optItem.get();
                            item.deductForOrder(command.getOrderId(), Quantity.of(orderItem.getQty()));
                            return repository.save(item)
                                    .thenAccept(saved -> {
                                        List<DomainEvent> events = saved.getUncommittedEvents();
                                        for (DomainEvent event : events) {
                                            eventPublisher.publish(event);
                                        }
                                        saved.clearUncommittedEvents();
                                    });
                        } else {
                            // Logic: if not found, we should arguably publish StockDeductionRejected for
                            // that item?
                            // Need to support creating events without aggregate or on new aggregate?
                            // For now, skip.
                            return CompletableFuture.completedFuture(null);
                        }
                    });
            futures.add(deduction);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
