package com.usermanagement.domain.exception;

public class InvalidNameException extends DomainException {
    public InvalidNameException(String reason) {
        super("INVALID_NAME", "Invalid name: " + reason);
    }
}
