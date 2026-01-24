package com.ecommerce.cart.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class Quantity implements Serializable {
    private final int value;

    private Quantity(int value) {
        if (value < 1) { // Cart quantity must be >= 1
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.value = value;
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    public int getValue() {
        return value;
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Quantity quantity = (Quantity) o;
        return value == quantity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
