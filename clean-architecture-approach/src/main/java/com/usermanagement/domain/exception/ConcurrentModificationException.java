package com.usermanagement.domain.exception;

public class ConcurrentModificationException extends DomainException {
    public ConcurrentModificationException() {
        super("CONCURRENT_MODIFICATION", "The resource was modified by another request. Please refresh and try again.");
    }

    @Override
    public int getHttpStatus() {
        return 409; // Conflict
    }
}
