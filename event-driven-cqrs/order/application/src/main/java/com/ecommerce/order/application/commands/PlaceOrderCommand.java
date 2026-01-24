package com.ecommerce.order.application.commands;

import com.ecommerce.order.domain.valueobjects.CustomerInfo;
import com.ecommerce.order.domain.valueobjects.ShippingAddress;
import com.ecommerce.shared.common.commands.Command;

public class PlaceOrderCommand implements Command<Void> {
    private final String commandId;
    private final String guestToken;
    private final CustomerInfo customer;
    private final ShippingAddress address;
    private final String idempotencyKey;

    public PlaceOrderCommand(String commandId, String guestToken, CustomerInfo customer, ShippingAddress address,
            String idempotencyKey) {
        this.commandId = commandId;
        this.guestToken = guestToken;
        this.customer = customer;
        this.address = address;
        this.idempotencyKey = idempotencyKey;
    }

    @Override
    public String getCommandId() {
        return commandId;
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

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
