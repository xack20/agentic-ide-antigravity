package com.usermanagement.domain.exception;

public class AccountLockedException extends DomainException {
    public AccountLockedException(long minutesRemaining) {
        super("ACCOUNT_LOCKED",
                "Account is temporarily locked. Try again in " + minutesRemaining + " minutes.");
    }

    @Override
    public int getHttpStatus() {
        return 423; // Locked
    }
}
