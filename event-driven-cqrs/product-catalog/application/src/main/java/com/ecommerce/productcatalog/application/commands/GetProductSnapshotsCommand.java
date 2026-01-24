package com.ecommerce.productcatalog.application.commands;

import com.ecommerce.shared.common.commands.Command;
import java.util.List;

public class GetProductSnapshotsCommand implements Command<Void> {
    private final String commandId;
    private final String orderId;
    private final List<String> productIds;

    public GetProductSnapshotsCommand(String commandId, String orderId, List<String> productIds) {
        this.commandId = commandId;
        this.orderId = orderId;
        this.productIds = productIds;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<String> getProductIds() {
        return productIds;
    }
}
