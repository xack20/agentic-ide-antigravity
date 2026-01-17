package com.usermanagement.domain.enums;

/**
 * Predefined role names in the system.
 */
public enum RoleName {
    /**
     * Super administrator with all permissions.
     */
    SUPER_ADMIN(1000, "Super Administrator"),

    /**
     * Administrator with user management permissions.
     */
    ADMIN(100, "Administrator"),

    /**
     * Regular authenticated user.
     */
    USER(10, "User"),

    /**
     * Guest with limited read-only access.
     */
    GUEST(1, "Guest");

    private final int privilegeLevel;
    private final String displayName;

    RoleName(int privilegeLevel, String displayName) {
        this.privilegeLevel = privilegeLevel;
        this.displayName = displayName;
    }

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if this role has higher or equal privilege than another.
     */
    public boolean hasHigherOrEqualPrivilegeThan(RoleName other) {
        return this.privilegeLevel >= other.privilegeLevel;
    }

    /**
     * Checks if this role can assign the target role.
     * A role can only assign roles with lower privilege level.
     */
    public boolean canAssign(RoleName target) {
        return this.privilegeLevel > target.privilegeLevel;
    }
}
