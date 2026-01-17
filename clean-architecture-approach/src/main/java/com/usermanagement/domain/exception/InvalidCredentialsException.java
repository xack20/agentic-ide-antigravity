package com.usermanagement.domain.exception;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Invalid email or password");
    }

    @Override
    public int getHttpStatus() {
        return 401;
    }
}
