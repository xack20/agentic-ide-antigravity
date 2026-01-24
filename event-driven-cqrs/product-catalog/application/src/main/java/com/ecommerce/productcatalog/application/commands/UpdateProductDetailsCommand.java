package com.ecommerce.productcatalog.application.commands;

import com.ecommerce.shared.common.commands.Command;
import java.util.UUID;

/**
 * Command to update product details (name and description).
 */
public class UpdateProductDetailsCommand implements Command<Void> {

    private final String commandId;
    private final String productId;
    private final String name;
    private final String description;

    public UpdateProductDetailsCommand(String productId, String name, String description) {
        this.commandId = UUID.randomUUID().toString();
        this.productId = productId;
        this.name = name;
        this.description = description;
    }

    public UpdateProductDetailsCommand(String commandId, String productId, String name, String description) {
        this.commandId = commandId;
        this.productId = productId;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
