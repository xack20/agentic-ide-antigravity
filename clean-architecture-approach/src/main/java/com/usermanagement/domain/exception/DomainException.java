package com.usermanagement.domain.exception;

/**
 * Base class for all domain exceptions.
 * These represent business rule violations.
 */
public abstract class DomainException extends RuntimeException {
    private final String code;

    protected DomainException(String code, String message) {
        super(message);
        this.code = code;
    }

    protected DomainException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns HTTP status code for this exception.
     * Subclasses can override if needed.
     */
    public int getHttpStatus() {
        return 400; // Bad Request by default
    }
}
