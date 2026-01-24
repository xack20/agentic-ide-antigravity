package com.ecommerce.cart.application.handlers;

import com.ecommerce.cart.application.commands.UpdateCartItemQtyCommand;
import com.ecommerce.cart.application.ports.CartRepository;
import com.ecommerce.cart.domain.aggregates.ShoppingCart;
import com.ecommerce.cart.domain.valueobjects.GuestToken;
import com.ecommerce.cart.domain.valueobjects.ProductId;
import com.ecommerce.cart.domain.valueobjects.Quantity;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UpdateCartItemQtyCommandHandler implements CommandHandler<UpdateCartItemQtyCommand, Void> {

    private final CartRepository repository;
    private final EventPublisher eventPublisher;

    public UpdateCartItemQtyCommandHandler(CartRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(UpdateCartItemQtyCommand command) {
        GuestToken token = GuestToken.of(command.getGuestToken());

        return repository.findByGuestToken(token)
                .thenCompose(optCart -> {
                    if (optCart.isEmpty()) {
                        throw new IllegalArgumentException("Cart not found for guest: " + command.getGuestToken());
                    }
                    ShoppingCart cart = optCart.get();

                    cart.updateItemQty(
                            ProductId.of(command.getProductId()),
                            Quantity.of(command.getQty()));

                    return repository.save(cart);
                })
                .thenAccept(saved -> {
                    List<DomainEvent> events = saved.getUncommittedEvents();
                    for (DomainEvent event : events) {
                        eventPublisher.publish(event);
                    }
                    saved.clearUncommittedEvents();
                });
    }
}
