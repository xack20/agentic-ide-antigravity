package com.ecommerce.cart.application.handlers;

import com.ecommerce.cart.application.commands.RemoveCartItemCommand;
import com.ecommerce.cart.application.ports.CartRepository;
import com.ecommerce.cart.domain.aggregates.ShoppingCart;
import com.ecommerce.cart.domain.valueobjects.GuestToken;
import com.ecommerce.cart.domain.valueobjects.ProductId;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RemoveCartItemCommandHandler implements CommandHandler<RemoveCartItemCommand, Void> {

    private final CartRepository repository;
    private final EventPublisher eventPublisher;

    public RemoveCartItemCommandHandler(CartRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(RemoveCartItemCommand command) {
        GuestToken token = GuestToken.of(command.getGuestToken());

        return repository.findByGuestToken(token)
                .thenCompose(optCart -> {
                    if (optCart.isEmpty()) {
                        // Idempotent: if cart doesn't exist, item is definitely not in it.
                        return CompletableFuture.completedFuture(null);
                    }
                    ShoppingCart cart = optCart.get();

                    cart.removeItem(ProductId.of(command.getProductId()));

                    return repository.save(cart);
                })
                .thenAccept(saved -> {
                    if (saved != null) {
                        List<DomainEvent> events = saved.getUncommittedEvents();
                        for (DomainEvent event : events) {
                            eventPublisher.publish(event);
                        }
                        saved.clearUncommittedEvents();
                    }
                });
    }
}
