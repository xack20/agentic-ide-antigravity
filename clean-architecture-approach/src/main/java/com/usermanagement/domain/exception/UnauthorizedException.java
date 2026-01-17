package com.usermanagement.domain.exception;

public class UnauthorizedException extends DomainException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }

    @Override
    public int getHttpStatus() {
        return 403; // Forbidden
    }
}
