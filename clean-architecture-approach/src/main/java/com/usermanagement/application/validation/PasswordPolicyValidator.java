package com.usermanagement.application.validation;

import com.usermanagement.domain.exception.WeakPasswordException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates passwords against policy requirements.
 */
@Component
public class PasswordPolicyValidator {

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[^a-zA-Z0-9]");

    private final int minLength;
    private final int minCategories;
    private final Set<String> commonPasswords;

    public PasswordPolicyValidator(
            @Value("${policy.password.min-length:12}") int minLength,
            @Value("${policy.password.min-categories:3}") int minCategories) {
        this.minLength = minLength;
        this.minCategories = minCategories;
        // In production, load from file or external service
        this.commonPasswords = Set.of(
                "password123", "123456789012", "qwertyuiop12",
                "letmein12345", "welcome12345", "admin1234567");
    }

    /**
     * Validates a password against policy requirements.
     * 
     * @throws WeakPasswordException if password doesn't meet requirements
     */
    public void validate(String password, String email, String username, String fullName) {
        List<String> violations = new ArrayList<>();

        if (password == null || password.length() < minLength) {
            violations.add("Password must be at least " + minLength + " characters");
        }

        if (password != null) {
            int categories = countCategories(password);
            if (categories < minCategories) {
                violations.add("Password must contain at least " + minCategories +
                        " of: uppercase, lowercase, digit, special character");
            }

            if (containsPersonalInfo(password, email, username, fullName)) {
                violations.add("Password cannot contain email, username, or name");
            }

            if (isCommonPassword(password)) {
                violations.add("Password is too common");
            }
        }

        if (!violations.isEmpty()) {
            throw new WeakPasswordException(violations);
        }
    }

    private int countCategories(String password) {
        int count = 0;
        if (UPPERCASE.matcher(password).find())
            count++;
        if (LOWERCASE.matcher(password).find())
            count++;
        if (DIGIT.matcher(password).find())
            count++;
        if (SPECIAL.matcher(password).find())
            count++;
        return count;
    }

    private boolean containsPersonalInfo(String password, String email,
            String username, String fullName) {
        String lower = password.toLowerCase();

        if (email != null) {
            String localPart = email.split("@")[0].toLowerCase();
            if (localPart.length() >= 4 && lower.contains(localPart)) {
                return true;
            }
        }

        if (username != null && username.length() >= 4 &&
                lower.contains(username.toLowerCase())) {
            return true;
        }

        if (fullName != null) {
            String[] names = fullName.toLowerCase().split("\\s+");
            for (String name : names) {
                if (name.length() >= 4 && lower.contains(name)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isCommonPassword(String password) {
        return commonPasswords.contains(password.toLowerCase());
    }

    /**
     * Gets the minimum required password length.
     */
    public int getMinLength() {
        return minLength;
    }
}
