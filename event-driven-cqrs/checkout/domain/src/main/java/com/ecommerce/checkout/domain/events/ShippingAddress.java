package com.ecommerce.checkout.domain.events;

import java.io.Serializable;
import java.util.Objects;

public class ShippingAddress implements Serializable {
    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;

    public ShippingAddress(String addressLine1, String addressLine2, String city, String state, String zipCode,
            String country) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
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
        return Objects.equals(addressLine1, that.addressLine1) &&
                Objects.equals(addressLine2, that.addressLine2) &&
                Objects.equals(city, that.city) &&
                Objects.equals(state, that.state) &&
                Objects.equals(zipCode, that.zipCode) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressLine1, addressLine2, city, state, zipCode, country);
    }
}
