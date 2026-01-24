package com.ecommerce.order.queryapi.repositories;

import com.ecommerce.order.queryapi.models.OrderDetailView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDetailViewRepository extends MongoRepository<OrderDetailView, String> {
    Optional<OrderDetailView> findByOrderNumber(String orderNumber);
}
