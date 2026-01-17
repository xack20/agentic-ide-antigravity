package com.usermanagement.domain.enums;

/**
 * Reasons for user account deactivation.
 * Used for audit and compliance purposes.
 */
public enum DeactivationReason {
    /**
     * User requested account deactivation.
     */
    USER_REQUESTED("User requested account closure"),

    /**
     * Fraudulent activity detected.
     */
    FRAUD("Fraudulent activity detected"),

    /**
     * Policy violation by the user.
     */
    POLICY_VIOLATION("Terms of service violation"),

    /**
     * Security concern (compromised account).
     */
    SECURITY("Security concern"),

    /**
     * Compliance or legal requirement.
     */
    COMPLIANCE("Legal/compliance requirement"),

    /**
     * Account inactive for extended period.
     */
    INACTIVITY("Extended inactivity"),

    /**
     * Administrative action.
     */
    ADMINISTRATIVE("Administrative decision");

    private final String description;

    DeactivationReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if this reason requires admin action for reactivation.
     */
    public boolean requiresAdminReactivation() {
        return this == FRAUD || this == POLICY_VIOLATION || this == SECURITY || this == COMPLIANCE;
    }

    /**
     * Checks if this reason requires password reset on reactivation.
     */
    public boolean requiresPasswordResetOnReactivation() {
        return this == SECURITY || this == FRAUD;
    }
}
