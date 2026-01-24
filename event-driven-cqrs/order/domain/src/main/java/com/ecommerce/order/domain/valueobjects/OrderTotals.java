package com.ecommerce.order.domain.valueobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class OrderTotals implements Serializable {
    private final BigDecimal subtotal;
    private final BigDecimal shippingFee;
    private final BigDecimal total;

    public OrderTotals(BigDecimal subtotal, BigDecimal shippingFee, BigDecimal total) {
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.total = total;
    }

    public static OrderTotals of(BigDecimal subtotal, BigDecimal shippingFee) {
        BigDecimal total = subtotal.add(shippingFee);
        return new OrderTotals(subtotal, shippingFee, total);
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderTotals that = (OrderTotals) o;
        return Objects.equals(subtotal, that.subtotal) &&
                Objects.equals(shippingFee, that.shippingFee) &&
                Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtotal, shippingFee, total);
    }
}
