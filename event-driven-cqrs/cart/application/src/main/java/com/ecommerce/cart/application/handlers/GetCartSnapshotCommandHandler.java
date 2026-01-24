package com.ecommerce.cart.application.handlers;

import com.ecommerce.cart.application.commands.GetCartSnapshotCommand;
import com.ecommerce.cart.application.ports.CartRepository;
import com.ecommerce.cart.domain.events.CartSnapshotProvided;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
public class GetCartSnapshotCommandHandler implements CommandHandler<GetCartSnapshotCommand, Void> {

    private final CartRepository repository;
    private final EventPublisher eventPublisher;

    public GetCartSnapshotCommandHandler(CartRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(GetCartSnapshotCommand command) {
        return repository.findByGuestToken(command.getGuestToken())
                .thenCompose(optCart -> {
                    // Even if cart is empty or null, we provide empty snapshot
                    // The business logic/validation happens downstream (Saga Manager check)
                    var items = optCart.map(cart -> {
                        var map = new HashMap<String, Integer>();
                        cart.getItems().forEach(item -> map.put(item.getProductId(), item.getQuantity()));
                        return map;
                    }).orElse(new HashMap<>());

                    CartSnapshotProvided event = new CartSnapshotProvided(
                            command.getOrderId(),
                            command.getGuestToken(),
                            items);

                    return eventPublisher.publish(event);
                });
    }

    @Override
    public Class<GetCartSnapshotCommand> getCommandType() {
        return GetCartSnapshotCommand.class;
    }
}
