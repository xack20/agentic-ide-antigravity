package com.ecommerce.cart.application.commands;

import com.ecommerce.shared.common.commands.Command;

public class UpdateCartItemQtyCommand implements Command<Void> {
    private final String commandId;
    private final String guestToken;
    private final String productId;
    private final int qty;

    public UpdateCartItemQtyCommand(String commandId, String guestToken, String productId, int qty) {
        this.commandId = commandId;
        this.guestToken = guestToken;
        this.productId = productId;
        this.qty = qty;
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

    public int getQty() {
        return qty;
    }
}
