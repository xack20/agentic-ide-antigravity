package com.ecommerce.productcatalog.domain.valueobjects;

import java.util.Objects;

/**
 * Value object representing a product name with validation.
 */
public final class ProductName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 200;

    private final String value;

    private ProductName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Product name must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
        }
        this.value = trimmed;
    }

    public static ProductName of(String value) {
        return new ProductName(value);
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
        ProductName that = (ProductName) o;
        return Objects.equals(value, that.value);
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
