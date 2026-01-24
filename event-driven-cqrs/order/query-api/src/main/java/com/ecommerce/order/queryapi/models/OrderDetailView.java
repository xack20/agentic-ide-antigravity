package com.ecommerce.order.queryapi.models;

import com.ecommerce.order.domain.aggregates.OrderLineItem;
import com.ecommerce.order.domain.valueobjects.CustomerInfo;
import com.ecommerce.order.domain.valueobjects.OrderTotals;
import com.ecommerce.order.domain.valueobjects.ShippingAddress;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "order_views")
public class OrderDetailView {
    @Id
    private String orderId;

    @Indexed(unique = true)
    private String orderNumber;

    @Indexed
    private String guestToken;

    private CustomerInfo customer;
    private ShippingAddress address;
    private List<OrderLineItem> items;
    private OrderTotals totals;
    private String paymentStatus;
    private String orderStatus;
    private Instant createdAt;

    public OrderDetailView() {
    }

    public OrderDetailView(String orderId, String orderNumber, String guestToken, CustomerInfo customer,
            ShippingAddress address, List<OrderLineItem> items, OrderTotals totals) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.guestToken = guestToken;
        this.customer = customer;
        this.address = address;
        this.items = items;
        this.totals = totals;
        this.paymentStatus = "Pending";
        this.orderStatus = "Created";
        this.createdAt = Instant.now();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public CustomerInfo getCustomer() {
        return customer;
    }

    public ShippingAddress getAddress() {
        return address;
    }

    public List<OrderLineItem> getItems() {
        return items;
    }

    public OrderTotals getTotals() {
        return totals;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
