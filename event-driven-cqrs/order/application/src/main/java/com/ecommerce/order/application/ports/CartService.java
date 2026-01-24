package com.ecommerce.order.application.ports;

import com.ecommerce.order.domain.valueobjects.OrderId;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CartService {
    // Returns Map<ProductId, Qty>
    CompletableFuture<Map<String, Integer>> getCartItems(String guestToken);
}
