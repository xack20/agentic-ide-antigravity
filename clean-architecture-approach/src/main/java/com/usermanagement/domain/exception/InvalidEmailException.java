package com.usermanagement.domain.exception;

public class InvalidEmailException extends DomainException {
    public InvalidEmailException(String reason) {
        super("INVALID_EMAIL", "Invalid email: " + reason);
    }
}
