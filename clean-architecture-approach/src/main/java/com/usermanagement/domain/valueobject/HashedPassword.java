package com.usermanagement.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing a hashed password.
 * Never stores or exposes the plain text password.
 */
public final class HashedPassword {
    private final String hash;
    private final Instant createdAt;

    private HashedPassword(String hash, Instant createdAt) {
        this.hash = hash;
        this.createdAt = createdAt;
    }

    /**
     * Creates a HashedPassword from an already hashed value.
     * Used when loading from database.
     */
    public static HashedPassword fromHash(String hash) {
        return fromHash(hash, Instant.now());
    }

    /**
     * Creates a HashedPassword from an already hashed value with timestamp.
     */
    public static HashedPassword fromHash(String hash, Instant createdAt) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }
        return new HashedPassword(hash, createdAt != null ? createdAt : Instant.now());
    }

    /**
     * Returns the hashed password value.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Returns when the password was created/changed.
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HashedPassword that = (HashedPassword) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    @Override
    public String toString() {
        // Never expose the hash in toString
        return "[PROTECTED]";
    }
}
