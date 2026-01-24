package com.ecommerce.cart.application.handlers;

import com.ecommerce.cart.application.commands.ClearCartCommand;
import com.ecommerce.cart.application.ports.CartRepository;
import com.ecommerce.cart.domain.aggregates.ShoppingCart;
import com.ecommerce.cart.domain.valueobjects.GuestToken;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ClearCartCommandHandler implements CommandHandler<ClearCartCommand, Void> {

    private final CartRepository repository;
    private final EventPublisher eventPublisher;

    public ClearCartCommandHandler(CartRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(ClearCartCommand command) {
        GuestToken token = GuestToken.of(command.getGuestToken());

        return repository.findByGuestToken(command.getGuestToken())
                .thenCompose(optCart -> {
                    if (optCart.isEmpty())
                        return CompletableFuture.completedFuture(null);

                    var cart = optCart.get();
                    cart.clearCart(command.getOrderId()); // Pass through

                    return repository.save(cart)
                            .thenAccept(saved -> {
                                saved.getUncommittedEvents().forEach(eventPublisher::publish);
                                saved.clearUncommittedEvents();
                            });
                });
    }

    @Override
    public Class<ClearCartCommand> getCommandType() {
        return ClearCartCommand.class;
    }
}
