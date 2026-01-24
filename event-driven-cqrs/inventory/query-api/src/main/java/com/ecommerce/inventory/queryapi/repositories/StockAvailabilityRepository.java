package com.ecommerce.inventory.queryapi.repositories;

import com.ecommerce.inventory.queryapi.models.StockAvailabilityView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAvailabilityRepository extends MongoRepository<StockAvailabilityView, String> {
}
