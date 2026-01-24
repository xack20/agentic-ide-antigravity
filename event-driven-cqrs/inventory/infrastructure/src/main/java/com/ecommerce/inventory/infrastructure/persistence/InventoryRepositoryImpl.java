package com.ecommerce.inventory.infrastructure.persistence;

import com.ecommerce.inventory.application.ports.InventoryRepository;
import com.ecommerce.inventory.domain.aggregates.InventoryItem;
import com.ecommerce.inventory.domain.valueobjects.ProductId;
import com.ecommerce.inventory.domain.valueobjects.Quantity;
import com.ecommerce.shared.common.persistence.ConcurrencyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryMongoRepository mongoRepository;

    public InventoryRepositoryImpl(InventoryMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public CompletableFuture<Optional<InventoryItem>> findById(ProductId id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<InventoryDocument> doc = mongoRepository.findById(id.getValue());
            return doc.map(this::toAggregate);
        });
    }

    @Override
    public CompletableFuture<InventoryItem> save(InventoryItem aggregate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InventoryDocument doc = toDocument(aggregate);
                doc.setUpdatedAt(Instant.now());
                if (doc.getCreatedAt() == null) {
                    doc.setCreatedAt(Instant.now());
                }

                InventoryDocument saved = mongoRepository.save(doc);
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

    private InventoryItem toAggregate(InventoryDocument doc) {
        return InventoryItem.reconstitute(
                ProductId.of(doc.getProductId()),
                Quantity.of(doc.getQuantity()),
                doc.getVersion() != null ? doc.getVersion() : 0);
    }

    private InventoryDocument toDocument(InventoryItem aggregate) {
        InventoryDocument doc = new InventoryDocument();
        doc.setProductId(aggregate.getId().getValue());
        doc.setQuantity(aggregate.getQuantity().getValue());

        // For existing aggregates (isNew=false), set the version to enable optimistic
        // locking and update
        if (!aggregate.isNew()) {
            doc.setVersion(aggregate.getVersion());
        }
        // isNew=true (new aggregates): version stays null, enabling proper insert
        // behavior
        return doc;
    }
}
