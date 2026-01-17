package com.usermanagement.domain.valueobject;

import com.usermanagement.domain.exception.InvalidNameException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class FullNameTest {

    @Test
    @DisplayName("Should create valid full name")
    void shouldCreateValidFullName() {
        FullName name = FullName.of("John Doe");
        assertEquals("John Doe", name.getValue());
    }

    @Test
    @DisplayName("Should normalize whitespace")
    void shouldNormalizeWhitespace() {
        FullName name = FullName.of("  John    Doe  ");
        assertEquals("John Doe", name.getValue());
    }

    @Test
    @DisplayName("Should extract first and last name")
    void shouldExtractFirstAndLastName() {
        FullName name = FullName.of("John Michael Doe");
        assertEquals("John", name.getFirstName());
        assertEquals("Michael Doe", name.getLastName());
    }

    @Test
    @DisplayName("Should handle single name")
    void shouldHandleSingleName() {
        FullName name = FullName.of("Prince");
        assertEquals("Prince", name.getFirstName());
        assertEquals("", name.getLastName());
    }

    @Test
    @DisplayName("Should throw for too short name")
    void shouldThrowForTooShortName() {
        assertThrows(InvalidNameException.class, () -> FullName.of("A"));
    }

    @Test
    @DisplayName("Should throw for too long name")
    void shouldThrowForTooLongName() {
        String longName = "A".repeat(81);
        assertThrows(InvalidNameException.class, () -> FullName.of(longName));
    }

    @Test
    @DisplayName("Should throw for restricted words")
    void shouldThrowForRestrictedWords() {
        assertThrows(InvalidNameException.class, () -> FullName.of("Admin User"));
        assertThrows(InvalidNameException.class, () -> FullName.of("System Admin"));
    }

    @Test
    @DisplayName("Should throw for only symbols")
    void shouldThrowForOnlySymbols() {
        assertThrows(InvalidNameException.class, () -> FullName.of("123456"));
        assertThrows(InvalidNameException.class, () -> FullName.of("@#$%^"));
    }

    @Test
    @DisplayName("Names should be equal by normalized value")
    void namesShouldBeEqualByNormalizedValue() {
        FullName name1 = FullName.of("John Doe");
        FullName name2 = FullName.of("john doe");

        assertEquals(name1, name2);
    }
}
