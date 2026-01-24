package com.ecommerce.order.infrastructure.persistence;

import com.ecommerce.order.application.ports.OrderRepository;
import com.ecommerce.order.domain.aggregates.Order;
import com.ecommerce.order.domain.valueobjects.IdempotencyKey;
import com.ecommerce.order.domain.valueobjects.OrderId;
import com.ecommerce.order.domain.valueobjects.OrderNumber;
import com.ecommerce.shared.common.persistence.ConcurrencyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMongoRepository mongoRepository;

    public OrderRepositoryImpl(OrderMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public CompletableFuture<Optional<Order>> findById(OrderId id) {
        return CompletableFuture.supplyAsync(() -> mongoRepository.findById(id.getValue()).map(this::toAggregate));
    }

    @Override
    public CompletableFuture<Optional<Order>> findByIdempotencyKey(IdempotencyKey key) {
        return CompletableFuture
                .supplyAsync(() -> mongoRepository.findByIdempotencyKey(key.getValue()).map(this::toAggregate));
    }

    @Override
    public CompletableFuture<Order> save(Order aggregate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OrderDocument doc = toDocument(aggregate);
                doc.setUpdatedAt(Instant.now());
                if (doc.getCreatedAt() == null) {
                    doc.setCreatedAt(Instant.now());
                }

                OrderDocument saved = mongoRepository.save(doc);
                return toAggregate(saved);
            } catch (OptimisticLockingFailureException ex) {
                throw new ConcurrencyException(
                        aggregate.getId().getValue(),
                        aggregate.getVersion(),
                        -1);
            }
        });
    }

    private Order toAggregate(OrderDocument doc) {
        return Order.reconstitute(
                OrderId.of(doc.getOrderId()),
                OrderNumber.of(doc.getOrderNumber()),
                doc.getGuestToken(),
                doc.getCustomer(),
                doc.getAddress(),
                doc.getItems(),
                doc.getTotals(),
                doc.getPaymentStatus(),
                doc.getOrderStatus(),
                IdempotencyKey.of(doc.getIdempotencyKey()),
                doc.getVersion() != null ? doc.getVersion() : 0);
    }

    private OrderDocument toDocument(Order aggregate) {
        OrderDocument doc = new OrderDocument();
        doc.setOrderId(aggregate.getId().getValue());
        doc.setOrderNumber(aggregate.getOrderNumber().getValue());
        doc.setGuestToken(aggregate.getGuestToken());
        doc.setCustomer(aggregate.getCustomer());
        doc.setAddress(aggregate.getAddress());
        doc.setItems(aggregate.getItems());
        doc.setTotals(aggregate.getTotals());
        doc.setPaymentStatus(aggregate.getPaymentStatus());
        doc.setOrderStatus(aggregate.getOrderStatus());
        doc.setIdempotencyKey(aggregate.getIdempotencyKey().getValue());

        if (!aggregate.isNew()) {
            doc.setVersion(aggregate.getVersion());
        }
        return doc;
    }
}
