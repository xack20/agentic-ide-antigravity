package com.usermanagement.domain.exception;

public class InvalidPhoneException extends DomainException {
    public InvalidPhoneException(String reason) {
        super("INVALID_PHONE", "Invalid phone number: " + reason);
    }
}
