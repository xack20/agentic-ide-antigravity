package com.ecommerce.inventory.application.commands;

import com.ecommerce.shared.common.commands.Command;
import java.util.Map;

public class ValidateStockBatchCommand implements Command<Void> {
    private final String commandId;
    private final String orderId;
    private final Map<String, Integer> items; // ProductId -> Qty

    public ValidateStockBatchCommand(String commandId, String orderId, Map<String, Integer> items) {
        this.commandId = commandId;
        this.orderId = orderId;
        this.items = items;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Map<String, Integer> getItems() {
        return items;
    }
}
