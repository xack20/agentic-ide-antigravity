package com.ecommerce.productcatalog.domain.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a Stock Keeping Unit (SKU).
 * Immutable and unique identifier for a distinct product item.
 */
public final class Sku {

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Za-z0-9-_]{3,50}$");

    private final String value;

    private Sku(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be null or empty");
        }
        String trimmed = value.trim();
        if (!SKU_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException(
                    "SKU must be alphanumeric (dash/underscore allowed) and between 3-50 chars: " + trimmed);
        }
        this.value = trimmed;
    }

    public static Sku of(String value) {
        return new Sku(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sku sku = (Sku) o;
        return Objects.equals(value, sku.value);
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
