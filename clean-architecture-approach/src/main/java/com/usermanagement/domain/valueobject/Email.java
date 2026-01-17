package com.usermanagement.domain.valueobject;

import com.usermanagement.domain.exception.InvalidEmailException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a validated and normalized email address.
 */
public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final String value; // Normalized value
    private final String originalValue; // Original input

    private Email(String value, String originalValue) {
        this.value = value;
        this.originalValue = originalValue;
    }

    /**
     * Creates an Email value object with validation and normalization.
     *
     * @param email          the raw email input
     * @param normalizeGmail if true, applies Gmail-specific normalization
     * @return validated and normalized Email
     * @throws InvalidEmailException if email is invalid
     */
    public static Email of(String email, boolean normalizeGmail) {
        if (email == null || email.isBlank()) {
            throw new InvalidEmailException("Email cannot be empty");
        }

        String trimmed = email.trim();

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }

        if (trimmed.length() > 254) {
            throw new InvalidEmailException("Email exceeds maximum length of 254 characters");
        }

        String normalized = normalize(trimmed, normalizeGmail);
        return new Email(normalized, trimmed);
    }

    /**
     * Creates an Email with default normalization settings.
     */
    public static Email of(String email) {
        return of(email, true);
    }

    private static String normalize(String email, boolean normalizeGmail) {
        String lower = email.toLowerCase();

        if (!normalizeGmail) {
            return lower;
        }

        String[] parts = lower.split("@");
        String local = parts[0];
        String domain = parts[1];

        // Gmail normalization: remove dots and everything after +
        if (domain.equals("gmail.com") || domain.equals("googlemail.com")) {
            // Remove everything after +
            int plusIndex = local.indexOf('+');
            if (plusIndex > 0) {
                local = local.substring(0, plusIndex);
            }
            // Remove dots from local part
            local = local.replace(".", "");
            // Normalize googlemail.com to gmail.com
            domain = "gmail.com";
        }

        return local + "@" + domain;
    }

    /**
     * Extracts the domain from the email address.
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    /**
     * Returns the normalized email value (for storage and comparison).
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the original email value (for display purposes).
     */
    public String getOriginalValue() {
        return originalValue;
    }

    /**
     * Returns a masked version of the email for logging/display.
     */
    public String getMasked() {
        String[] parts = originalValue.split("@");
        String local = parts[0];
        String domain = parts[1];

        if (local.length() <= 2) {
            return local.charAt(0) + "***@" + domain;
        }
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
