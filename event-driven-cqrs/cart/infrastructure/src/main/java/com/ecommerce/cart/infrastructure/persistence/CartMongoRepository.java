package com.ecommerce.cart.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartMongoRepository extends MongoRepository<CartDocument, String> {
    Optional<CartDocument> findByGuestToken(String guestToken);
}
