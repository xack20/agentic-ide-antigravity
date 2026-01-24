package com.ecommerce.order.domain.aggregates;

import com.ecommerce.order.domain.events.OrderCartClearRequested;
import com.ecommerce.order.domain.events.OrderCreated;
import com.ecommerce.order.domain.events.OrderStockCommitRequested;
import com.ecommerce.order.domain.valueobjects.*;
import com.ecommerce.shared.common.domain.AggregateRoot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Order extends AggregateRoot<OrderId> {

    private OrderId id;
    private OrderNumber orderNumber;
    private String guestToken;
    private CustomerInfo customer;
    private ShippingAddress address;
    private List<OrderLineItem> items;
    private OrderTotals totals;
    private String paymentStatus; // "Pending"
    private String orderStatus; // "Created"
    private IdempotencyKey idempotencyKey;

    private int version;
    private boolean isNew = false;

    private Order() {
    }

    public static Order create(
            OrderId id,
            OrderNumber orderNumber,
            String guestToken,
            CustomerInfo customer,
            ShippingAddress address,
            List<OrderLineItem> items,
            OrderTotals totals,
            IdempotencyKey idempotencyKey) {

        Order order = new Order();
        order.id = id;
        order.orderNumber = orderNumber;
        order.guestToken = guestToken;
        order.customer = customer;
        order.address = address;
        order.items = items;
        order.totals = totals;
        order.paymentStatus = "Pending";
        order.orderStatus = "Created";
        order.idempotencyKey = idempotencyKey;
        order.version = 0;
        order.isNew = true;

        order.raiseEvent(new OrderCreated(
                id.getValue(),
                orderNumber.getValue(),
                guestToken,
                customer,
                address,
                items,
                totals));

        // Side effects for other contexts are now managed by the Checkout Saga

        return order;
    }

    // Reconstitute method for Repo
    public static Order reconstitute(
            OrderId id,
            OrderNumber orderNumber,
            String guestToken,
            CustomerInfo customer,
            ShippingAddress address,
            List<OrderLineItem> items,
            OrderTotals totals,
            String paymentStatus,
            String orderStatus,
            IdempotencyKey idempotencyKey,
            int version) {

        Order order = new Order();
        order.id = id;
        order.orderNumber = orderNumber;
        order.guestToken = guestToken;
        order.customer = customer;
        order.address = address;
        order.items = items;
        order.totals = totals;
        order.paymentStatus = paymentStatus;
        order.orderStatus = orderStatus;
        order.idempotencyKey = idempotencyKey;
        order.version = version;
        order.isNew = false;
        return order;
    }

    @Override
    public OrderId getId() {
        return id;
    }

    // Getters
    public OrderNumber getOrderNumber() {
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

    public IdempotencyKey getIdempotencyKey() {
        return idempotencyKey;
    }

    public boolean isNew() {
        return isNew;
    }

    public void submit() {
        if (!"Created".equals(this.orderStatus)) {
            throw new IllegalStateException("Order in status " + this.orderStatus + " cannot be submitted");
        }
        this.orderStatus = "Placed";
        // raiseEvent(new OrderSubmitted(id.getValue())); // Could add event later
    }

    public void cancel(String reason) {
        this.orderStatus = "Cancelled";
        // raiseEvent(new OrderCancelled(id.getValue(), reason));
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    protected void setVersion(int version) {
        this.version = version;
    }
}
