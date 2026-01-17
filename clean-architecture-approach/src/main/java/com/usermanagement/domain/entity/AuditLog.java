package com.usermanagement.domain.entity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Audit log entry for tracking user actions.
 */
public class AuditLog {
    private final String id;
    private final String action;
    private final String actorId; // Who performed the action
    private final String targetId; // Target user
    private final String targetType; // Type of target (USER, ROLE, etc.)
    private final Map<String, Object> oldValues;
    private final Map<String, Object> newValues;
    private final String reason;
    private final AuditMetadata metadata;
    private final Instant timestamp;

    private AuditLog(String id, String action, String actorId, String targetId,
            String targetType, Map<String, Object> oldValues,
            Map<String, Object> newValues, String reason,
            AuditMetadata metadata, Instant timestamp) {
        this.id = id;
        this.action = action;
        this.actorId = actorId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.reason = reason;
        this.metadata = metadata;
        this.timestamp = timestamp;
    }

    public static AuditLog create(String action, String actorId, String targetId,
            String targetType, Map<String, Object> oldValues,
            Map<String, Object> newValues, String reason,
            String ipAddress, String userAgent, String correlationId) {
        return new AuditLog(
                UUID.randomUUID().toString(),
                action, actorId, targetId, targetType,
                oldValues, newValues, reason,
                AuditMetadata.of(ipAddress, userAgent, correlationId),
                Instant.now());
    }

    public static AuditLog reconstitute(String id, String action, String actorId,
            String targetId, String targetType,
            Map<String, Object> oldValues,
            Map<String, Object> newValues,
            String reason, AuditMetadata metadata,
            Instant timestamp) {
        return new AuditLog(id, action, actorId, targetId, targetType,
                oldValues, newValues, reason, metadata, timestamp);
    }

    public String getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getActorId() {
        return actorId;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public Map<String, Object> getOldValues() {
        return oldValues;
    }

    public Map<String, Object> getNewValues() {
        return newValues;
    }

    public String getReason() {
        return reason;
    }

    public AuditMetadata getMetadata() {
        return metadata;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
