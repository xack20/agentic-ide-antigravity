package com.ecommerce.order.domain.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class ShippingAddress implements Serializable {
    private final String line1;
    private final String city;
    private final String postalCode;
    private final String country;

    public ShippingAddress(String line1, String city, String postalCode, String country) {
        if (line1 == null || line1.trim().isEmpty()) {
            throw new IllegalArgumentException("Address Line 1 is required");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required");
        }
        this.line1 = line1;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getLine1() {
        return line1;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ShippingAddress that = (ShippingAddress) o;
        return Objects.equals(line1, that.line1) &&
                Objects.equals(city, that.city) &&
                Objects.equals(postalCode, that.postalCode) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line1, city, postalCode, country);
    }
}
