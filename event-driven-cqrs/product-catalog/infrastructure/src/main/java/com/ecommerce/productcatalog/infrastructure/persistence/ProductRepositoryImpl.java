package com.ecommerce.productcatalog.infrastructure.persistence;

import com.ecommerce.productcatalog.application.ports.ProductRepository;
import com.ecommerce.productcatalog.domain.aggregates.Product;
import com.ecommerce.productcatalog.domain.valueobjects.*;
import com.ecommerce.shared.common.persistence.ConcurrencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * MongoDB implementation of ProductRepository.
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryImpl.class);

    private final ProductMongoRepository mongoRepository;

    public ProductRepositoryImpl(ProductMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public CompletableFuture<Optional<Product>> findById(ProductId id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<ProductDocument> doc = mongoRepository.findById(id.getValue());
            return doc.map(this::toAggregate);
        });
    }

    @Override
    public CompletableFuture<Product> save(Product aggregate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ProductDocument doc = toDocument(aggregate);
                doc.setUpdatedAt(Instant.now());
                if (doc.getCreatedAt() == null) {
                    doc.setCreatedAt(Instant.now());
                }

                ProductDocument saved = mongoRepository.save(doc);
                logger.debug("Saved product: id={}, version={}", saved.getId(), saved.getVersion());

                return toAggregate(saved);
            } catch (OptimisticLockingFailureException ex) {
                throw new ConcurrencyException(
                        aggregate.getId().getValue(),
                        aggregate.getVersion(),
                        -1);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> exists(ProductId id) {
        return CompletableFuture.supplyAsync(() -> mongoRepository.existsById(id.getValue()));
    }

    @Override
    public CompletableFuture<Void> deleteById(ProductId id) {
        return CompletableFuture.runAsync(() -> mongoRepository.deleteById(id.getValue()));
    }

    @Override
    public CompletableFuture<Boolean> existsBySku(String sku) {
        return CompletableFuture.supplyAsync(() -> mongoRepository.existsBySku(sku));
    }

    private Product toAggregate(ProductDocument doc) {
        return Product.reconstitute(
                ProductId.of(doc.getId()),
                ProductName.of(doc.getName()),
                doc.getDescription(),
                Money.of(doc.getPrice(), doc.getCurrency()),
                doc.getSku(),
                ProductStatus.valueOf(doc.getStatus()),
                doc.getVersion() != null ? doc.getVersion() : 0);
    }

    private ProductDocument toDocument(Product aggregate) {
        ProductDocument doc = new ProductDocument();
        doc.setId(aggregate.getId().getValue());
        doc.setName(aggregate.getName().getValue());
        doc.setDescription(aggregate.getDescription());
        doc.setPrice(aggregate.getPrice().getAmount());
        doc.setCurrency(aggregate.getPrice().getCurrencyCode());
        doc.setSku(aggregate.getSku());
        doc.setStatus(aggregate.getStatus().name());
        doc.setVersion(aggregate.getVersion());
        return doc;
    }
}
