package com.usermanagement.domain.exception;

public class RateLimitExceededException extends DomainException {
    public RateLimitExceededException(String action) {
        super("RATE_LIMIT_EXCEEDED", "Too many " + action + " attempts. Please try again later.");
    }

    @Override
    public int getHttpStatus() {
        return 429; // Too Many Requests
    }
}
