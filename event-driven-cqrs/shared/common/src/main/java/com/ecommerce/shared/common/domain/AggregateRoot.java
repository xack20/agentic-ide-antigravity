package com.ecommerce.shared.common.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all Aggregate Roots in the domain.
 * Follows DDD pattern with event collection for state changes.
 *
 * @param <TId> The type of the aggregate identifier
 */
public abstract class AggregateRoot<TId> {
    
    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();
    
    protected abstract TId getId();
    
    public abstract int getVersion();
    
    protected abstract void setVersion(int version);
    
    /**
     * Get all uncommitted events that have been raised since the last commit.
     */
    public List<DomainEvent> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }
    
    /**
     * Clear all uncommitted events after they have been persisted/published.
     */
    public void clearUncommittedEvents() {
        uncommittedEvents.clear();
    }
    
    /**
     * Raise a domain event. This should be called after state change.
     */
    protected void raiseEvent(DomainEvent event) {
        uncommittedEvents.add(event);
    }
    
    /**
     * Increment version for optimistic concurrency.
     */
    protected void incrementVersion() {
        setVersion(getVersion() + 1);
    }
}
