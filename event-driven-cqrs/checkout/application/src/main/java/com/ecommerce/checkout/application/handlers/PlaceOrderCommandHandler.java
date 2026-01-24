package com.ecommerce.checkout.application.handlers;

import com.ecommerce.checkout.application.commands.PlaceOrderCommand;
import com.ecommerce.checkout.domain.events.CheckoutRequested;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PlaceOrderCommandHandler implements CommandHandler<PlaceOrderCommand, Void> {

    private final EventPublisher eventPublisher;

    public PlaceOrderCommandHandler(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(PlaceOrderCommand command) {
        return processOrder(command);
    }

    private CompletableFuture<Void> processOrder(PlaceOrderCommand command) {
        // Generate Order ID now to track the saga
        String orderId = UUID.randomUUID().toString();

        // Emit CheckoutRequested event to start the Saga
        CheckoutRequested event = new CheckoutRequested(
                orderId,
                command.getGuestToken(),
                command.getCustomer(),
                command.getAddress(),
                command.getIdempotencyKey());

        return eventPublisher.publish(event);
    }

    @Override
    public Class<PlaceOrderCommand> getCommandType() {
        return PlaceOrderCommand.class;
    }
}
