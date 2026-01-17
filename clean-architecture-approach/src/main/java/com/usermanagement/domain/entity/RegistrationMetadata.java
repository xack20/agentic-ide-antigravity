package com.usermanagement.domain.entity;

import java.time.Instant;

/**
 * Metadata captured during user registration for audit.
 */
public class RegistrationMetadata {
    private final Instant registeredAt;
    private final String ipAddress;
    private final String userAgent;
    private final String deviceFingerprint;
    private final String channel;

    private RegistrationMetadata(Instant registeredAt, String ipAddress,
            String userAgent, String deviceFingerprint, String channel) {
        this.registeredAt = registeredAt;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.deviceFingerprint = deviceFingerprint;
        this.channel = channel;
    }

    public static RegistrationMetadata of(String ipAddress, String userAgent,
            String deviceFingerprint, String channel) {
        return new RegistrationMetadata(Instant.now(), ipAddress, userAgent, deviceFingerprint, channel);
    }

    public static RegistrationMetadata reconstitute(Instant registeredAt, String ipAddress,
            String userAgent, String deviceFingerprint, String channel) {
        return new RegistrationMetadata(registeredAt, ipAddress, userAgent, deviceFingerprint, channel);
    }

    public Instant getRegisteredAt() {
        return registeredAt;
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

    public String getChannel() {
        return channel;
    }
}
