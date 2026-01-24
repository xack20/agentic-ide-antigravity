package com.ecommerce.productcatalog.application.commands;

import com.ecommerce.shared.common.commands.Command;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to create a new product.
 */
public class CreateProductCommand implements Command<CreateProductResult> {

    private final String commandId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String currency;
    private final String sku;

    public CreateProductCommand(String name, String description, BigDecimal price,
            String currency, String sku) {
        this.commandId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.sku = sku;
    }

    public CreateProductCommand(String commandId, String name, String description,
            BigDecimal price, String currency, String sku) {
        this.commandId = commandId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.sku = sku;
    }

    @Override
    public String getCommandId() {
        return commandId;
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

    public String getSku() {
        return sku;
    }
}
