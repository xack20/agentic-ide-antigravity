package com.usermanagement.domain.entity;

/**
 * Metadata associated with an audit log entry.
 */
public class AuditMetadata {
    private final String ipAddress;
    private final String userAgent;
    private final String correlationId;

    private AuditMetadata(String ipAddress, String userAgent, String correlationId) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.correlationId = correlationId;
    }

    public static AuditMetadata of(String ipAddress, String userAgent, String correlationId) {
        return new AuditMetadata(ipAddress, userAgent, correlationId);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
