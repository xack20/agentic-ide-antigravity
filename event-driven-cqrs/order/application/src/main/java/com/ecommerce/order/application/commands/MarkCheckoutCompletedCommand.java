package com.ecommerce.order.application.commands;

import com.ecommerce.shared.common.commands.Command;

public class MarkCheckoutCompletedCommand implements Command<Void> {
    private final String commandId;
    private final String orderId;

    public MarkCheckoutCompletedCommand(String commandId, String orderId) {
        this.commandId = commandId;
        this.orderId = orderId;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getOrderId() {
        return orderId;
    }
}
