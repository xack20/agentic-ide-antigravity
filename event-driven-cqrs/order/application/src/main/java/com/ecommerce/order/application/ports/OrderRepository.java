package com.ecommerce.order.application.ports;

import com.ecommerce.order.domain.aggregates.Order;
import com.ecommerce.order.domain.valueobjects.IdempotencyKey;
import com.ecommerce.order.domain.valueobjects.OrderId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface OrderRepository {
    CompletableFuture<Order> save(Order order);

    CompletableFuture<Optional<Order>> findById(OrderId id);

    CompletableFuture<Optional<Order>> findByIdempotencyKey(IdempotencyKey key);
}
