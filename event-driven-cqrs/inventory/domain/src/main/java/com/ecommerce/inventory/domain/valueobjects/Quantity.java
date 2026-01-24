package com.ecommerce.inventory.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class Quantity implements Serializable {
    private final int value;

    private Quantity(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
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

    public Quantity subtract(Quantity other) {
        return new Quantity(this.value - other.value);
    }

    public boolean isGreaterThanOrEqual(Quantity other) {
        return this.value >= other.value;
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
