package com.ecommerce.productcatalog.application.commands;

import com.ecommerce.shared.common.commands.Command;
import java.util.UUID;

/**
 * Command to deactivate a product.
 */
public class DeactivateProductCommand implements Command<Void> {

    private final String commandId;
    private final String productId;

    public DeactivateProductCommand(String productId) {
        this.commandId = UUID.randomUUID().toString();
        this.productId = productId;
    }

    public DeactivateProductCommand(String commandId, String productId) {
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
