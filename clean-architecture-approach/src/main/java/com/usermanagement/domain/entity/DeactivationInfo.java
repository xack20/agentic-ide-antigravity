package com.usermanagement.domain.entity;

import com.usermanagement.domain.enums.DeactivationReason;
import com.usermanagement.domain.valueobject.UserId;

import java.time.Instant;

/**
 * Information about account deactivation.
 */
public class DeactivationInfo {
    private final DeactivationReason reason;
    private final String evidence;
    private final UserId deactivatedBy;
    private final Instant deactivatedAt;

    private DeactivationInfo(DeactivationReason reason, String evidence,
            UserId deactivatedBy, Instant deactivatedAt) {
        this.reason = reason;
        this.evidence = evidence;
        this.deactivatedBy = deactivatedBy;
        this.deactivatedAt = deactivatedAt;
    }

    public static DeactivationInfo of(DeactivationReason reason, String evidence, UserId deactivatedBy) {
        return new DeactivationInfo(reason, evidence, deactivatedBy, Instant.now());
    }

    public static DeactivationInfo reconstitute(DeactivationReason reason, String evidence,
            UserId deactivatedBy, Instant deactivatedAt) {
        return new DeactivationInfo(reason, evidence, deactivatedBy, deactivatedAt);
    }

    public DeactivationReason getReason() {
        return reason;
    }

    public String getEvidence() {
        return evidence;
    }

    public UserId getDeactivatedBy() {
        return deactivatedBy;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

    /**
     * Checks if admin reactivation is required based on deactivation reason.
     */
    public boolean requiresAdminReactivation() {
        return reason.requiresAdminReactivation();
    }

    /**
     * Checks if password reset is required on reactivation.
     */
    public boolean requiresPasswordResetOnReactivation() {
        return reason.requiresPasswordResetOnReactivation();
    }
}
