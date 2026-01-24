package com.ecommerce.order.domain.aggregates;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderLineItem implements Serializable {
    private final String productId;
    private final String sku;
    private final String productName;
    private final BigDecimal unitPrice;
    private final int quantity;
    private final BigDecimal lineTotal;

    public OrderLineItem(String productId, String sku, String productName, BigDecimal unitPrice, int quantity) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
