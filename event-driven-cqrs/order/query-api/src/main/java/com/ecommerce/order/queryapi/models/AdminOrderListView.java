package com.ecommerce.order.queryapi.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "admin_order_list_views")
public class AdminOrderListView {
    @Id
    private String orderId;
    private String orderNumber;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalAmount;
    private String orderStatus;
    private Instant createdAt;

    public AdminOrderListView(String orderId, String orderNumber, String customerName, String customerPhone,
            BigDecimal totalAmount, String orderStatus, Instant createdAt) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
