package com.usermanagement.domain.entity;

import java.time.Duration;
import java.time.Instant;

/**
 * Tracks login attempt information for rate limiting and lockout.
 */
public class LoginAttemptInfo {
    private final int count;
    private final Instant lastAttemptAt;
    private final Instant lockedUntil;

    private LoginAttemptInfo(int count, Instant lastAttemptAt, Instant lockedUntil) {
        this.count = count;
        this.lastAttemptAt = lastAttemptAt;
        this.lockedUntil = lockedUntil;
    }

    public static LoginAttemptInfo initial() {
        return new LoginAttemptInfo(0, null, null);
    }

    public static LoginAttemptInfo reconstitute(int count, Instant lastAttemptAt, Instant lockedUntil) {
        return new LoginAttemptInfo(count, lastAttemptAt, lockedUntil);
    }

    /**
     * Creates a new LoginAttemptInfo with incremented failure count.
     */
    public LoginAttemptInfo incrementFailure() {
        return new LoginAttemptInfo(count + 1, Instant.now(), lockedUntil);
    }

    /**
     * Creates a new LoginAttemptInfo with lockout applied.
     */
    public LoginAttemptInfo withLockout(Duration lockoutDuration) {
        return new LoginAttemptInfo(count, lastAttemptAt, Instant.now().plus(lockoutDuration));
    }

    /**
     * Checks if the account is currently locked out.
     */
    public boolean isLockedOut(int lockoutMinutes) {
        if (lockedUntil == null) {
            return false;
        }
        return Instant.now().isBefore(lockedUntil);
    }

    /**
     * Returns minutes remaining in lockout, or 0 if not locked.
     */
    public long getMinutesRemaining() {
        if (lockedUntil == null || Instant.now().isAfter(lockedUntil)) {
            return 0;
        }
        return Duration.between(Instant.now(), lockedUntil).toMinutes();
    }

    public int getCount() {
        return count;
    }

    public Instant getLastAttemptAt() {
        return lastAttemptAt;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }
}
