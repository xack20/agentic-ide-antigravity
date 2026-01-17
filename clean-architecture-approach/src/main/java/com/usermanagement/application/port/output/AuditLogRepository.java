package com.usermanagement.application.port.output;

import com.usermanagement.domain.entity.AuditLog;

import java.time.Instant;
import java.util.List;

/**
 * Output port for audit log persistence operations.
 */
public interface AuditLogRepository {

    /**
     * Saves an audit log entry.
     */
    AuditLog save(AuditLog auditLog);

    /**
     * Finds audit logs by target ID.
     */
    List<AuditLog> findByTargetId(String targetId, int limit);

    /**
     * Finds audit logs by actor ID.
     */
    List<AuditLog> findByActorId(String actorId, int limit);

    /**
     * Finds audit logs by action type.
     */
    List<AuditLog> findByAction(String action, Instant after, int limit);

    /**
     * Finds audit logs for a target within a time range.
     */
    List<AuditLog> findByTargetIdAndTimeRange(String targetId, Instant start, Instant end);
}
