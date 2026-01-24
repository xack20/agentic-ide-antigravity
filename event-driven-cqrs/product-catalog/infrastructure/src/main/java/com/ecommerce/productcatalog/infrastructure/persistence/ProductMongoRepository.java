package com.ecommerce.productcatalog.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for ProductDocument.
 */
@Repository
public interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {

    boolean existsBySku(String sku);

    Optional<ProductDocument> findBySku(String sku);
}
