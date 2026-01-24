package com.ecommerce.cart.application.commands;

import com.ecommerce.shared.common.commands.Command;

public class ClearCartCommand implements Command<Void> {
    private final String commandId;
    private final String guestToken;

    public ClearCartCommand(String commandId, String guestToken) {
        this.commandId = commandId;
        this.guestToken = guestToken;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getGuestToken() {
        return guestToken;
    }
}
