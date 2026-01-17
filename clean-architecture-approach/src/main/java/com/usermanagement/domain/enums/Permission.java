package com.usermanagement.domain.enums;

/**
 * System permissions that can be assigned to roles.
 */
public enum Permission {
    // User Management
    USER_READ("View user profiles and list users"),
    USER_WRITE("Update user information"),
    USER_DELETE("Delete user accounts"),
    USER_STATUS_MANAGE("Activate/deactivate user accounts"),
    USER_EXPORT("Export user data"),

    // Role Management
    ROLE_READ("View roles and permissions"),
    ROLE_MANAGE("Assign and revoke roles"),

    // Audit
    AUDIT_READ("View audit logs"),

    // System
    SYSTEM_CONFIG("Modify system configuration");

    private final String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
