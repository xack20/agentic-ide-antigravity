package com.ecommerce.inventory.application.commands;

import com.ecommerce.shared.common.commands.Command;
import java.util.List;

public class DeductStockForOrderCommand implements Command<Void> {
    private final String commandId;
    private final String orderId;
    private final List<OrderItem> items;

    public DeductStockForOrderCommand(String commandId, String orderId, List<OrderItem> items) {
        this.commandId = commandId;
        this.orderId = orderId;
        this.items = items;
    }

    @Override
    public String getCommandId() {
        return commandId;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public static class OrderItem {
        private final String productId;
        private final int qty;

        public OrderItem(String productId, int qty) {
            this.productId = productId;
            this.qty = qty;
        }

        public String getProductId() {
            return productId;
        }

        public int getQty() {
            return qty;
        }
    }
}
