package com.ecommerce.productcatalog.application.commands;

import com.ecommerce.shared.common.commands.Command;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to change product price.
 */
public class ChangeProductPriceCommand implements Command<Void> {

    private final String commandId;
    private final String productId;
    private final BigDecimal newPrice;
    private final String currency;

    public ChangeProductPriceCommand(String productId, BigDecimal newPrice, String currency) {
        this.commandId = UUID.randomUUID().toString();
        this.productId = productId;
        this.newPrice = newPrice;
        this.currency = currency;
    }

    public ChangeProductPriceCommand(String commandId, String productId, BigDecimal newPrice, String currency) {
        this.commandId = commandId;
        this.productId = productId;
        this.newPrice = newPrice;
        this.currency = currency;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getProductId() {
        return productId;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public String getCurrency() {
        return currency;
    }
}
