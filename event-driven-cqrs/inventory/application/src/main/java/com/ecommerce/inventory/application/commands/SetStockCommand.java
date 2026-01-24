package com.ecommerce.inventory.application.commands;

import com.ecommerce.shared.common.commands.Command;

public class SetStockCommand implements Command<Void> {
    private final String commandId;
    private final String productId;
    private final int newQty;
    private final String reason;

    public SetStockCommand(String commandId, String productId, int newQty, String reason) {
        this.commandId = commandId;
        this.productId = productId;
        this.newQty = newQty;
        this.reason = reason;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getProductId() {
        return productId;
    }

    public int getNewQty() {
        return newQty;
    }

    public String getReason() {
        return reason;
    }
}
