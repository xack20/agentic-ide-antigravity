package com.ecommerce.checkout.commandapi.dto;

public record PlaceOrderRequest(
        String guestToken,
        CustomerDto customer,
        AddressDto address,
        String idempotencyKey) {
    public record CustomerDto(String firstName, String lastName, String email, String phone) {
    }

    public record AddressDto(String line1, String line2, String city, String state, String zipCode, String country) {
    }
}
