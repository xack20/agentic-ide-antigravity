package com.ecommerce.productcatalog.application.commands;

import com.ecommerce.shared.common.commands.Command;

import java.util.UUID;

/**
 * Command to activate a product (make available for sale).
 */
public class ActivateProductCommand implements Command<ActivateProductResult> {

    private final String commandId;
    private final String productId;

    public ActivateProductCommand(String productId) {
        this.commandId = UUID.randomUUID().toString();
        this.productId = productId;
    }

    public ActivateProductCommand(String commandId, String productId) {
        this.commandId = commandId;
        this.productId = productId;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getProductId() {
        return productId;
    }
}
