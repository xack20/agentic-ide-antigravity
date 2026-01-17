package com.usermanagement.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String userId) {
        super("USER_NOT_FOUND", "User not found: " + userId);
    }

    @Override
    public int getHttpStatus() {
        return 404;
    }
}
