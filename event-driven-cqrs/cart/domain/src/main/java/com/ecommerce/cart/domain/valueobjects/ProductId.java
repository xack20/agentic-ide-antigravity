package com.ecommerce.cart.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class ProductId implements Serializable {
    private final String value;

    private ProductId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ProductId cannot be empty");
        }
        this.value = value;
    }

    public static ProductId of(String value) {
        return new ProductId(value);
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
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
