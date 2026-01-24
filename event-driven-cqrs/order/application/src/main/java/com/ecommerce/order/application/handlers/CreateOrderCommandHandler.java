package com.ecommerce.order.application.handlers;

import com.ecommerce.order.application.commands.CreateOrderCommand;
import com.ecommerce.order.application.ports.OrderRepository;
import com.ecommerce.order.domain.aggregates.Order;
import com.ecommerce.order.domain.valueobjects.IdempotencyKey;
import com.ecommerce.order.domain.valueobjects.OrderId;
import com.ecommerce.order.domain.valueobjects.OrderNumber;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CreateOrderCommandHandler implements CommandHandler<CreateOrderCommand, Void> {

    private final OrderRepository repository;
    private final EventPublisher eventPublisher;

    public CreateOrderCommandHandler(OrderRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(CreateOrderCommand command) {
        Order order = Order.create(
                OrderId.of(command.getOrderId()),
                OrderNumber.generate(),
                command.getGuestToken(),
                command.getCustomer(),
                command.getAddress(),
                command.getItems(),
                command.getTotals(),
                IdempotencyKey.of(command.getIdempotencyKey()));

        return repository.save(order)
                .thenAccept(saved -> {
                    List<DomainEvent> events = saved.getUncommittedEvents();
                    for (DomainEvent event : events) {
                        eventPublisher.publish(event);
                    }
                    saved.clearUncommittedEvents();
                });
    }

    @Override
    public Class<CreateOrderCommand> getCommandType() {
        return CreateOrderCommand.class;
    }
}
