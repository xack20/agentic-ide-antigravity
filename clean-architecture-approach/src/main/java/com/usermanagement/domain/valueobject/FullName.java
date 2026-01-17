package com.usermanagement.domain.valueobject;

import com.usermanagement.domain.exception.InvalidNameException;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Value object representing a validated full name.
 */
public final class FullName {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 80;

    // Pattern for names with only symbols/digits (invalid)
    private static final Pattern ONLY_SYMBOLS_DIGITS = Pattern.compile("^[^a-zA-Z\\p{L}]+$");

    // Simple profanity filter (in production, use a proper library)
    private static final Set<String> RESTRICTED_WORDS = Set.of(
            "admin", "root", "system", "support", "null", "undefined");

    private final String value;
    private final String normalized;

    private FullName(String value, String normalized) {
        this.value = value;
        this.normalized = normalized;
    }

    /**
     * Creates a FullName value object with validation.
     *
     * @param name the raw name input
     * @return validated FullName
     * @throws InvalidNameException if name is invalid
     */
    public static FullName of(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidNameException("Name cannot be empty");
        }

        String trimmed = name.trim().replaceAll("\\s+", " ");

        if (trimmed.length() < MIN_LENGTH) {
            throw new InvalidNameException("Name must be at least " + MIN_LENGTH + " characters");
        }

        if (trimmed.length() > MAX_LENGTH) {
            throw new InvalidNameException("Name cannot exceed " + MAX_LENGTH + " characters");
        }

        if (ONLY_SYMBOLS_DIGITS.matcher(trimmed).matches()) {
            throw new InvalidNameException("Name cannot contain only symbols or digits");
        }

        String normalized = trimmed.toLowerCase();

        for (String restricted : RESTRICTED_WORDS) {
            if (normalized.contains(restricted)) {
                throw new InvalidNameException("Name contains restricted words");
            }
        }

        return new FullName(trimmed, normalized);
    }

    /**
     * Returns the original name value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the normalized (lowercase) name for search.
     */
    public String getNormalized() {
        return normalized;
    }

    /**
     * Returns the first name (first word).
     */
    public String getFirstName() {
        int spaceIndex = value.indexOf(' ');
        return spaceIndex > 0 ? value.substring(0, spaceIndex) : value;
    }

    /**
     * Returns the last name (everything after first word).
     */
    public String getLastName() {
        int spaceIndex = value.indexOf(' ');
        return spaceIndex > 0 ? value.substring(spaceIndex + 1) : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FullName fullName = (FullName) o;
        return Objects.equals(normalized, fullName.normalized);
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalized);
    }

    @Override
    public String toString() {
        return value;
    }
}
