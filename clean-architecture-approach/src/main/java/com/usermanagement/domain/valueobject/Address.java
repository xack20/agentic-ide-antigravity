package com.usermanagement.domain.valueobject;

import java.util.Objects;

/**
 * Value object representing a postal address.
 */
public final class Address {
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;

    private Address(String street, String city, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    /**
     * Creates an Address with validation.
     */
    public static Address of(String street, String city, String postalCode, String country) {
        return new Address(
                street != null ? street.trim() : null,
                city != null ? city.trim() : null,
                postalCode != null ? postalCode.trim() : null,
                country != null ? country.trim().toUpperCase() : null);
    }

    /**
     * Creates an empty/null address.
     */
    public static Address empty() {
        return new Address(null, null, null, null);
    }

    public String getStreet() {
        return street;
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

    public boolean isEmpty() {
        return street == null && city == null && postalCode == null && country == null;
    }

    /**
     * Returns a formatted single-line address.
     */
    public String getFormatted() {
        StringBuilder sb = new StringBuilder();
        if (street != null)
            sb.append(street);
        if (city != null) {
            if (!sb.isEmpty())
                sb.append(", ");
            sb.append(city);
        }
        if (postalCode != null) {
            if (!sb.isEmpty())
                sb.append(" ");
            sb.append(postalCode);
        }
        if (country != null) {
            if (!sb.isEmpty())
                sb.append(", ");
            sb.append(country);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, postalCode, country);
    }
}
