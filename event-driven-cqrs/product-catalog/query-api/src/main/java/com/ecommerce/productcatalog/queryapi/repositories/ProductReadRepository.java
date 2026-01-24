package com.ecommerce.productcatalog.queryapi.repositories;

import com.ecommerce.productcatalog.queryapi.models.ProductReadModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for product read model queries.
 */
@Repository
public interface ProductReadRepository extends MongoRepository<ProductReadModel, String> {

    Page<ProductReadModel> findByStatus(String status, Pageable pageable);

    List<ProductReadModel> findByStatusIn(List<String> statuses);

    Optional<ProductReadModel> findBySku(String sku);

    Page<ProductReadModel> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
