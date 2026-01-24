package com.ecommerce.cart.commandapi.dto;

public record AddCartItemRequest(String productId, int qty) {
}
