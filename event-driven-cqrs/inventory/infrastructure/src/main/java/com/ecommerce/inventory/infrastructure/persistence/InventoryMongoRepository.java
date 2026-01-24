package com.ecommerce.inventory.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMongoRepository extends MongoRepository<InventoryDocument, String> {
}
