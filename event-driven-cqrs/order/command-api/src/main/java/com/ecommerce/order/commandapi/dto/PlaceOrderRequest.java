package com.ecommerce.order.commandapi.dto;

public record PlaceOrderRequest(
        String guestToken,
        CustomerDto customer,
        AddressDto address,
        String idempotencyKey) {
    public record CustomerDto(String name, String phone, String email) {
    }

    public record AddressDto(String line1, String city, String postalCode, String country) {
    }
}
