package com.ecommerce.inventory.application.handlers;

import com.ecommerce.inventory.application.commands.ValidateStockBatchCommand;
import com.ecommerce.inventory.application.ports.InventoryRepository;
import com.ecommerce.inventory.domain.events.StockBatchValidated;
import com.ecommerce.inventory.domain.valueobjects.ProductId;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ValidateStockBatchCommandHandler implements CommandHandler<ValidateStockBatchCommand, Void> {

    private final InventoryRepository repository;
    private final EventPublisher eventPublisher;

    public ValidateStockBatchCommandHandler(InventoryRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(ValidateStockBatchCommand command) {
        // Just checking stock, NOT deducting.
        // We iterate and check. If any fail, the whole batch fails.
        // Parallel check using CompletableFuture

        // This logic mimics the order check but purely on inventory side.

        // In real app we might want to check snapshots, but here we query live
        // inventory.

        // Issue: repository typically returns Optional<InventoryItem> by ID.
        // We can do this in parallel.

        return CompletableFuture.supplyAsync(() -> {
            // For simplicity in MVP, we do sequential read or parallel if possible.
            boolean allAvailable = true;
            String failedProduct = null;

            for (Map.Entry<String, Integer> entry : command.getItems().entrySet()) {
                String productId = entry.getKey();
                int requestedQty = entry.getValue();

                // Repository call (blocking in this lambda scope but lambda is async)
                // Wait, repo is async. We need to chain properly.
                // Let's defer to a helper.
                return checkAll(command);
            }
            return null;
        }).thenCompose(res -> res);
    }

    private CompletableFuture<Void> checkAll(ValidateStockBatchCommand command) {
        // Collect futures
        List<CompletableFuture<String>> checks = command.getItems().entrySet().stream()
                .map(entry -> repository.findById(ProductId.of(entry.getKey()))
                        .thenApply(optItem -> {
                            if (optItem.isEmpty())
                                return "Product " + entry.getKey() + " not found";
                            if (optItem.get().getQuantityAvailable().getValue() < entry.getValue())
                                return "Insufficient stock for " + entry.getKey();
                            return "OK";
                        }))
                .collect(java.util.stream.Collectors.toList());

        return CompletableFuture.allOf(checks.toArray(new CompletableFuture[0]))
                .thenCompose(v -> {
                    // Check results
                    String failure = null;
                    for (var future : checks) {
                        String res = future.join();
                        if (!"OK".equals(res)) {
                            failure = res;
                            break;
                        }
                    }

                    StockBatchValidated event = new StockBatchValidated(
                            command.getOrderId(),
                            failure == null,
                            failure);
                    return eventPublisher.publish(event);
                });
    }

    @Override
    public Class<ValidateStockBatchCommand> getCommandType() {
        return ValidateStockBatchCommand.class;
    }
}
