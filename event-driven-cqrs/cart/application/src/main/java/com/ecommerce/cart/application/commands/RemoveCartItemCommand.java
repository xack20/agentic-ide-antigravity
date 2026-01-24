package com.ecommerce.cart.application.commands;

import com.ecommerce.shared.common.commands.Command;

public class RemoveCartItemCommand implements Command<Void> {
    private final String commandId;
    private final String guestToken;
    private final String productId;

    public RemoveCartItemCommand(String commandId, String guestToken, String productId) {
        this.commandId = commandId;
        this.guestToken = guestToken;
        this.productId = productId;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public String getProductId() {
        return productId;
    }
}
