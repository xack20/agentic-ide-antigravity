package com.ecommerce.productcatalog.application.commands;

import com.ecommerce.shared.common.commands.Command;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to update an existing product.
 */
public class UpdateProductCommand implements Command<UpdateProductResult> {

    private final String commandId;
    private final String productId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String currency;

    public UpdateProductCommand(String productId, String name, String description,
            BigDecimal price, String currency) {
        this.commandId = UUID.randomUUID().toString();
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
    }

    public UpdateProductCommand(String commandId, String productId, String name,
            String description, BigDecimal price, String currency) {
        this.commandId = commandId;
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
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

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }
}
