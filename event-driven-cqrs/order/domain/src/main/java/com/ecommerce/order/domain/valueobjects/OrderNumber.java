package com.ecommerce.order.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

public class OrderNumber implements Serializable {
    private final String value;

    private OrderNumber(String value) {
        this.value = value;
    }

    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    public static OrderNumber generate() {
        // Simple random generation for MVP. In prod, use a sequence generator.
        int random = 100000 + new Random().nextInt(900000);
        return new OrderNumber("ORD-" + random);
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
        OrderNumber that = (OrderNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
