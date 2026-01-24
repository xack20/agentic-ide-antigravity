package com.ecommerce.shared.common.persistence;

/**
 * Exception thrown when a concurrency conflict is detected during save.
 */
public class ConcurrencyException extends RuntimeException {

    private final String aggregateId;
    private final int expectedVersion;
    private final int actualVersion;

    public ConcurrencyException(String aggregateId, int expectedVersion, int actualVersion) {
        super(String.format("Concurrency conflict for aggregate %s: expected version %d but found %d",
                aggregateId, expectedVersion, actualVersion));
        this.aggregateId = aggregateId;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public int getExpectedVersion() {
        return expectedVersion;
    }

    public int getActualVersion() {
        return actualVersion;
    }
}
