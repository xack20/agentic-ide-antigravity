package com.ecommerce.inventory.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class AdjustmentReason implements Serializable {
    private final String value;

    private AdjustmentReason(String value) {
        this.value = value;
    }

    public static AdjustmentReason of(String value) {
        return new AdjustmentReason(value);
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
        AdjustmentReason that = (AdjustmentReason) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
