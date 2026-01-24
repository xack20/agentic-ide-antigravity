package com.ecommerce.order.domain.events;

import com.ecommerce.order.domain.aggregates.OrderLineItem;
import com.ecommerce.order.domain.valueobjects.CustomerInfo;
import com.ecommerce.order.domain.valueobjects.OrderTotals;
import com.ecommerce.order.domain.valueobjects.ShippingAddress;
import com.ecommerce.shared.common.domain.DomainEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderCreated implements DomainEvent {
    private final UUID eventId;
    private final String orderId;
    private final String orderNumber;
    private final String guestToken;
    private final CustomerInfo customer;
    private final ShippingAddress address;
    private final List<OrderLineItem> items;
    private final OrderTotals totals;
    private final Instant occurredAt;

    public OrderCreated(String orderId, String orderNumber, String guestToken,
            CustomerInfo customer, ShippingAddress address,
            List<OrderLineItem> items, OrderTotals totals) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.guestToken = guestToken;
        this.customer = customer;
        this.address = address;
        this.items = items;
        this.totals = totals;
        this.occurredAt = Instant.now();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getAggregateId() {
        return orderId;
    }

    @Override
    public String getAggregateType() {
        return "Order";
    }

    @Override
    public String getEventType() {
        return "OrderCreated";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    // Getters
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
}
