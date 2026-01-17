package com.usermanagement.domain.valueobject;

import com.usermanagement.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    @DisplayName("Should create valid email")
    void shouldCreateValidEmail() {
        Email email = Email.of("test@example.com");
        assertEquals("test@example.com", email.getValue());
    }

    @Test
    @DisplayName("Should normalize Gmail addresses")
    void shouldNormalizeGmailAddresses() {
        // Dots are removed and plus aliases are stripped for Gmail
        Email email1 = Email.of("test.user+alias@gmail.com");
        Email email2 = Email.of("testuser@gmail.com");

        assertEquals(email1.getValue(), email2.getValue());
    }

    @Test
    @DisplayName("Should normalize googlemail.com to gmail.com")
    void shouldNormalizeGoogleMailDomain() {
        Email email = Email.of("user@googlemail.com");
        assertEquals("user@gmail.com", email.getValue());
    }

    @Test
    @DisplayName("Should not normalize non-Gmail emails")
    void shouldNotNormalizeNonGmailEmails() {
        Email email = Email.of("test.user+alias@company.com");
        assertEquals("test.user+alias@company.com", email.getValue());
    }

    @Test
    @DisplayName("Should throw exception for null email")
    void shouldThrowExceptionForNullEmail() {
        assertThrows(InvalidEmailException.class, () -> Email.of(null));
    }

    @Test
    @DisplayName("Should throw exception for empty email")
    void shouldThrowExceptionForEmptyEmail() {
        assertThrows(InvalidEmailException.class, () -> Email.of(""));
        assertThrows(InvalidEmailException.class, () -> Email.of("   "));
    }

    @Test
    @DisplayName("Should throw exception for invalid email format")
    void shouldThrowExceptionForInvalidFormat() {
        assertThrows(InvalidEmailException.class, () -> Email.of("invalid"));
        assertThrows(InvalidEmailException.class, () -> Email.of("@domain.com"));
        assertThrows(InvalidEmailException.class, () -> Email.of("user@"));
    }

    @Test
    @DisplayName("Should mask email correctly")
    void shouldMaskEmailCorrectly() {
        Email email = Email.of("testuser@example.com");
        String masked = email.getMasked();

        assertTrue(masked.startsWith("t"));
        assertTrue(masked.contains("***"));
        assertTrue(masked.endsWith("@example.com"));
    }

    @Test
    @DisplayName("Should extract domain correctly")
    void shouldExtractDomainCorrectly() {
        Email email = Email.of("user@example.com");
        assertEquals("example.com", email.getDomain());
    }

    @Test
    @DisplayName("Equal emails should be equal")
    void equalEmailsShouldBeEqual() {
        Email email1 = Email.of("user@example.com");
        Email email2 = Email.of("USER@EXAMPLE.COM");

        assertEquals(email1, email2);
    }
}
