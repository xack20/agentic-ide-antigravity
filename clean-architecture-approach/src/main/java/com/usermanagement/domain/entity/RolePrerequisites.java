package com.usermanagement.domain.entity;

import com.usermanagement.domain.enums.UserStatus;

/**
 * Prerequisites that must be met before a role can be assigned.
 */
public class RolePrerequisites {
    private final UserStatus requiredStatus;
    private final boolean requiresEmailVerified;
    private final boolean requiresPhoneVerified;

    private RolePrerequisites(UserStatus requiredStatus, boolean requiresEmailVerified,
            boolean requiresPhoneVerified) {
        this.requiredStatus = requiredStatus;
        this.requiresEmailVerified = requiresEmailVerified;
        this.requiresPhoneVerified = requiresPhoneVerified;
    }

    public static RolePrerequisites none() {
        return new RolePrerequisites(null, false, false);
    }

    public static RolePrerequisites of(UserStatus requiredStatus,
            boolean requiresEmailVerified,
            boolean requiresPhoneVerified) {
        return new RolePrerequisites(requiredStatus, requiresEmailVerified, requiresPhoneVerified);
    }

    /**
     * Checks if a user satisfies these prerequisites.
     */
    public boolean isSatisfiedBy(User user) {
        if (requiredStatus != null && user.getStatus() != requiredStatus) {
            return false;
        }
        if (requiresEmailVerified && !user.isEmailVerified()) {
            return false;
        }
        if (requiresPhoneVerified && !user.isPhoneVerified()) {
            return false;
        }
        return true;
    }

    public UserStatus getRequiredStatus() {
        return requiredStatus;
    }

    public boolean isRequiresEmailVerified() {
        return requiresEmailVerified;
    }

    public boolean isRequiresPhoneVerified() {
        return requiresPhoneVerified;
    }
}
