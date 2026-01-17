package com.usermanagement.application.validation;

import com.usermanagement.domain.exception.WeakPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyValidatorTest {

    private PasswordPolicyValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordPolicyValidator(12, 3);
    }

    @Test
    @DisplayName("Should accept valid password")
    void shouldAcceptValidPassword() {
        assertDoesNotThrow(() -> validator.validate("SecurePass123!", "user@example.com", null, "John Doe"));
    }

    @Test
    @DisplayName("Should reject short password")
    void shouldRejectShortPassword() {
        WeakPasswordException ex = assertThrows(WeakPasswordException.class,
                () -> validator.validate("Short1!", "user@example.com", null, "John"));

        assertTrue(ex.getViolations().stream()
                .anyMatch(v -> v.contains("at least 12 characters")));
    }

    @Test
    @DisplayName("Should reject password without enough character categories")
    void shouldRejectPasswordWithoutEnoughCategories() {
        WeakPasswordException ex = assertThrows(WeakPasswordException.class,
                () -> validator.validate("onlylowercase1", "user@example.com", null, "John"));

        assertTrue(ex.getViolations().stream()
                .anyMatch(v -> v.contains("at least 3")));
    }

    @Test
    @DisplayName("Should reject password containing email")
    void shouldRejectPasswordContainingEmail() {
        WeakPasswordException ex = assertThrows(WeakPasswordException.class,
                () -> validator.validate("testuser123!ABC", "testuser@example.com", null, "John"));

        assertTrue(ex.getViolations().stream()
                .anyMatch(v -> v.contains("email")));
    }

    @Test
    @DisplayName("Should reject password containing name")
    void shouldRejectPasswordContainingName() {
        WeakPasswordException ex = assertThrows(WeakPasswordException.class,
                () -> validator.validate("JohnDoe123!ABC", "user@example.com", null, "John Doe"));

        assertTrue(ex.getViolations().stream()
                .anyMatch(v -> v.contains("name")));
    }

    @Test
    @DisplayName("Should reject common password")
    void shouldRejectCommonPassword() {
        WeakPasswordException ex = assertThrows(WeakPasswordException.class,
                () -> validator.validate("password123", "user@example.com", null, "John"));

        assertTrue(ex.getViolations().stream()
                .anyMatch(v -> v.contains("common")));
    }

    @Test
    @DisplayName("Should accept password with special characters")
    void shouldAcceptPasswordWithSpecialCharacters() {
        assertDoesNotThrow(() -> validator.validate("MyP@ssw0rd!2024", "user@example.com", null, "John"));
    }
}
