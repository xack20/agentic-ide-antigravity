package com.ecommerce.order.application.handlers;

import com.ecommerce.order.application.commands.MarkCheckoutCompletedCommand;
import com.ecommerce.order.application.ports.OrderRepository;
import com.ecommerce.order.domain.valueobjects.OrderId;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MarkCheckoutCompletedCommandHandler implements CommandHandler<MarkCheckoutCompletedCommand, Void> {

    private final OrderRepository repository;
    private final EventPublisher eventPublisher;

    public MarkCheckoutCompletedCommandHandler(OrderRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(MarkCheckoutCompletedCommand command) {
        return repository.findById(OrderId.of(command.getOrderId()))
                .thenCompose(optOrder -> {
                    if (optOrder.isEmpty()) {
                        throw new IllegalArgumentException("Order not found: " + command.getOrderId());
                    }

                    com.ecommerce.order.domain.aggregates.Order order = optOrder.get();
                    // Status change logic in aggregate
                    order.submit(); // Assuming submit() marks it as placed/completed

                    return repository.save(order)
                            .thenAccept(saved -> {
                                List<DomainEvent> events = saved.getUncommittedEvents();
                                for (DomainEvent event : events) {
                                    eventPublisher.publish(event);
                                }
                                saved.clearUncommittedEvents();
                            });
                });
    }

    @Override
    public Class<MarkCheckoutCompletedCommand> getCommandType() {
        return MarkCheckoutCompletedCommand.class;
    }
}
