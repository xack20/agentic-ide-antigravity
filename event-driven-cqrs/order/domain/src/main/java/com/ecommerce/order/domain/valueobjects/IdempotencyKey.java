package com.ecommerce.order.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class IdempotencyKey implements Serializable {
    private final String value;

    private IdempotencyKey(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("IdempotencyKey cannot be empty");
        }
        this.value = value;
    }

    public static IdempotencyKey of(String value) {
        return new IdempotencyKey(value);
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
        IdempotencyKey that = (IdempotencyKey) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
