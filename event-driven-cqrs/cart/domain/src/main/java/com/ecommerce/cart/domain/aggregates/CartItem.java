package com.ecommerce.cart.domain.aggregates;

import com.ecommerce.cart.domain.valueobjects.ProductId;
import com.ecommerce.cart.domain.valueobjects.Quantity;

public class CartItem {
    private final ProductId productId;
    private final Quantity quantity;

    public CartItem(ProductId productId, Quantity quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public ProductId getProductId() {
        return productId;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
