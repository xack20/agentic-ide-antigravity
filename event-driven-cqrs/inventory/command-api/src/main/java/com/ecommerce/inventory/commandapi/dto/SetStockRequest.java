package com.ecommerce.inventory.commandapi.dto;

public record SetStockRequest(int newQty, String reason) {
}
