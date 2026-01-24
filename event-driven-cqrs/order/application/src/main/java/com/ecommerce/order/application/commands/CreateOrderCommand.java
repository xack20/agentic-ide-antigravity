package com.ecommerce.order.application.commands;

import com.ecommerce.order.domain.aggregates.OrderLineItem;
import com.ecommerce.order.domain.valueobjects.CustomerInfo;
import com.ecommerce.order.domain.valueobjects.OrderTotals;
import com.ecommerce.order.domain.valueobjects.ShippingAddress;
import com.ecommerce.shared.common.commands.Command;

import java.util.List;

public class CreateOrderCommand implements Command<Void> {
    private final String commandId;
    private final String orderId;
    private final String guestToken;
    private final CustomerInfo customer;
    private final ShippingAddress address;
    private final List<OrderLineItem> items;
    private final OrderTotals totals;
    private final String idempotencyKey;

    public CreateOrderCommand(String commandId, String orderId, String guestToken, CustomerInfo customer,
            ShippingAddress address, List<OrderLineItem> items, OrderTotals totals, String idempotencyKey) {
        this.commandId = commandId;
        this.orderId = orderId;
        this.guestToken = guestToken;
        this.customer = customer;
        this.address = address;
        this.items = items;
        this.totals = totals;
        this.idempotencyKey = idempotencyKey;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getOrderId() {
        return orderId;
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

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
