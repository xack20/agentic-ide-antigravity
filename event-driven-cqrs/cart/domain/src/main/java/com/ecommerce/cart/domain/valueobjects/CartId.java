package com.ecommerce.cart.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CartId implements Serializable {
    private final String value;

    private CartId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("CartId cannot be empty");
        }
        this.value = value;
    }

    public static CartId of(String value) {
        return new CartId(value);
    }

    public static CartId generate() {
        return new CartId(UUID.randomUUID().toString());
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
        CartId cartId = (CartId) o;
        return Objects.equals(value, cartId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
