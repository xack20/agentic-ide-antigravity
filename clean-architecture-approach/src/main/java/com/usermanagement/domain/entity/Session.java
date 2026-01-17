package com.usermanagement.domain.entity;

import com.usermanagement.domain.valueobject.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * User session entity for managing authentication sessions.
 */
public class Session {
    private final String id;
    private final UserId userId;
    private final String refreshTokenHash;
    private final String accessTokenJti;
    private final String ipAddress;
    private final String userAgent;
    private final String deviceFingerprint;
    private final Instant createdAt;
    private final Instant expiresAt;
    private Instant revokedAt;

    private Session(String id, UserId userId, String refreshTokenHash, String accessTokenJti,
            String ipAddress, String userAgent, String deviceFingerprint,
            Instant createdAt, Instant expiresAt, Instant revokedAt) {
        this.id = id;
        this.userId = userId;
        this.refreshTokenHash = refreshTokenHash;
        this.accessTokenJti = accessTokenJti;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.deviceFingerprint = deviceFingerprint;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
    }

    public static Session create(UserId userId, String refreshTokenHash, String accessTokenJti,
            String ipAddress, String userAgent, String deviceFingerprint,
            Instant expiresAt) {
        return new Session(
                UUID.randomUUID().toString(),
                userId, refreshTokenHash, accessTokenJti,
                ipAddress, userAgent, deviceFingerprint,
                Instant.now(), expiresAt, null);
    }

    public static Session reconstitute(String id, UserId userId, String refreshTokenHash,
            String accessTokenJti, String ipAddress, String userAgent,
            String deviceFingerprint, Instant createdAt,
            Instant expiresAt, Instant revokedAt) {
        return new Session(id, userId, refreshTokenHash, accessTokenJti,
                ipAddress, userAgent, deviceFingerprint, createdAt, expiresAt, revokedAt);
    }

    /**
     * Revokes this session.
     */
    public void revoke() {
        this.revokedAt = Instant.now();
    }

    /**
     * Checks if this session is valid (not expired and not revoked).
     */
    public boolean isValid() {
        return revokedAt == null && Instant.now().isBefore(expiresAt);
    }

    /**
     * Checks if this session is expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks if this session is revoked.
     */
    public boolean isRevoked() {
        return revokedAt != null;
    }

    public String getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public String getAccessTokenJti() {
        return accessTokenJti;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
