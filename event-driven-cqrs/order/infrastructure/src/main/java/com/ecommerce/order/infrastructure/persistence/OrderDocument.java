package com.ecommerce.order.infrastructure.persistence;

import com.ecommerce.order.domain.aggregates.OrderLineItem;
import com.ecommerce.order.domain.valueobjects.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
public class OrderDocument {
    @Id
    private String orderId;

    @Indexed(unique = true)
    private String orderNumber;

    @Indexed
    private String guestToken;

    @Indexed(unique = true)
    private String idempotencyKey;

    private CustomerInfo customer;
    private ShippingAddress address;
    private List<OrderLineItem> items;
    private OrderTotals totals;
    private String paymentStatus;
    private String orderStatus;

    @Version
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;

    public OrderDocument() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public void setGuestToken(String guestToken) {
        this.guestToken = guestToken;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public CustomerInfo getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerInfo customer) {
        this.customer = customer;
    }

    public ShippingAddress getAddress() {
        return address;
    }

    public void setAddress(ShippingAddress address) {
        this.address = address;
    }

    public List<OrderLineItem> getItems() {
        return items;
    }

    public void setItems(List<OrderLineItem> items) {
        this.items = items;
    }

    public OrderTotals getTotals() {
        return totals;
    }

    public void setTotals(OrderTotals totals) {
        this.totals = totals;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
