package com.ecommerce.order.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class CustomerInfo implements Serializable {
    private final String name;
    private final String phone;
    private final String email;

    public CustomerInfo(String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer Name is required");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer Phone is required");
        }
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomerInfo that = (CustomerInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email);
    }
}
