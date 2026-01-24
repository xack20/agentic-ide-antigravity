package com.ecommerce.productcatalog.domain.valueobjects;

import java.util.Objects;

/**
 * Value object representing a product description.
 * Can be empty but not null. Enforces max length.
 */
public final class ProductDescription {

    private static final int MAX_LENGTH = 2000;

    private final String value;

    private ProductDescription(String value) {
        if (value == null) {
            this.value = "";
            return;
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Description cannot exceed " + MAX_LENGTH + " characters");
        }
        this.value = trimmed;
    }

    public static ProductDescription of(String value) {
        return new ProductDescription(value);
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
        ProductDescription that = (ProductDescription) o;
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
