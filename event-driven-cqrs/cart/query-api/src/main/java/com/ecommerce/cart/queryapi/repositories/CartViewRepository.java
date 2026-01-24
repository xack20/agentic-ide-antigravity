package com.ecommerce.cart.queryapi.repositories;

import com.ecommerce.cart.queryapi.models.CartView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartViewRepository extends MongoRepository<CartView, String> {
    Optional<CartView> findByGuestToken(String guestToken);
}
