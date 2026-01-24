package com.ecommerce.cart.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class GuestToken implements Serializable {
    private final String value;

    private GuestToken(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("GuestToken cannot be empty");
        }
        this.value = value;
    }

    public static GuestToken of(String value) {
        return new GuestToken(value);
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
        GuestToken that = (GuestToken) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
