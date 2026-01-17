package com.usermanagement.domain.enums;

/**
 * Represents the possible states of a user account.
 */
public enum UserStatus {
    /**
     * User has registered but not verified email/phone yet.
     */
    PENDING_VERIFICATION,

    /**
     * User account is active and can log in.
     */
    ACTIVE,

    /**
     * User account has been deactivated (by self or admin).
     */
    DEACTIVATED,

    /**
     * User account is permanently banned.
     */
    BANNED;

    /**
     * Checks if this status allows login.
     */
    public boolean canLogin() {
        return this == ACTIVE;
    }

    /**
     * Checks if this status allows profile updates.
     */
    public boolean canUpdateProfile() {
        return this == ACTIVE || this == PENDING_VERIFICATION;
    }

    /**
     * Checks if transition to the target status is valid.
     */
    public boolean canTransitionTo(UserStatus target) {
        return switch (this) {
            case PENDING_VERIFICATION -> target == ACTIVE || target == DEACTIVATED;
            case ACTIVE -> target == DEACTIVATED || target == BANNED;
            case DEACTIVATED -> target == ACTIVE || target == BANNED;
            case BANNED -> false; // Cannot transition out of banned without special override
        };
    }
}
