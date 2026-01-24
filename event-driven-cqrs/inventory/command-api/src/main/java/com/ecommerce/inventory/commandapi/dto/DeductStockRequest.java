package com.ecommerce.inventory.commandapi.dto;

import java.util.List;

public record DeductStockRequest(String orderId, List<DeductionItem> items) {
    public record DeductionItem(String productId, int qty) {
    }
}
