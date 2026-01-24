package com.ecommerce.shared.common.persistence;

import com.ecommerce.shared.common.domain.AggregateRoot;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Generic repository interface for aggregate persistence.
 *
 * @param <TAggregate> The aggregate type
 * @param <TId>        The aggregate identifier type
 */
public interface Repository<TAggregate extends AggregateRoot<TId>, TId> {

    /**
     * Find an aggregate by its identifier.
     *
     * @param id The aggregate identifier
     * @return Optional containing the aggregate if found
     */
    CompletableFuture<Optional<TAggregate>> findById(TId id);

    /**
     * Save an aggregate (insert or update based on version).
     * Implements optimistic concurrency via version checking.
     *
     * @param aggregate The aggregate to save
     * @return The saved aggregate with updated version
     * @throws ConcurrencyException if version conflict detected
     */
    CompletableFuture<TAggregate> save(TAggregate aggregate);

    /**
     * Check if an aggregate exists by its identifier.
     *
     * @param id The aggregate identifier
     * @return true if exists, false otherwise
     */
    CompletableFuture<Boolean> exists(TId id);

    /**
     * Delete an aggregate by its identifier.
     *
     * @param id The aggregate identifier
     */
    CompletableFuture<Void> deleteById(TId id);
}
