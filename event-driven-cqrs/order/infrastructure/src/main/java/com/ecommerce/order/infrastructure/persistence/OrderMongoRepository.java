package com.ecommerce.order.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderMongoRepository extends MongoRepository<OrderDocument, String> {
    Optional<OrderDocument> findByOrderNumber(String orderNumber);

    Optional<OrderDocument> findByIdempotencyKey(String idempotencyKey);
}
