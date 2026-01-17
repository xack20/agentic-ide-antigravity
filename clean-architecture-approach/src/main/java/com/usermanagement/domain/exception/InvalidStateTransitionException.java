package com.usermanagement.domain.exception;

import com.usermanagement.domain.enums.UserStatus;

public class InvalidStateTransitionException extends DomainException {
    public InvalidStateTransitionException(UserStatus from, UserStatus to) {
        super("INVALID_STATE_TRANSITION",
                String.format("Cannot transition from %s to %s", from, to));
    }
}
