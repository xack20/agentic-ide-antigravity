package com.ecommerce.checkoutsaga.handler.saga.contracts;

import com.ecommerce.shared.common.commands.Command;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Local DTOs representing external contracts to maintain subsystem separation.
 */
public class SagaContracts {

    // --- External Events ---

    public record CartSnapshotProvided(String orderId, String guestToken, Map<String, Integer> items) {
    }

    public record ProductSnapshotsProvided(String orderId, List<ProductSnapshot> products) {
        public record ProductSnapshot(String id, String name, String sku, BigDecimal price, boolean active) {
        }
    }

    public record StockBatchValidated(String orderId, boolean success, String failureReason) {
    }

    public record StockDeductedForOrder(String orderId) {
    }

    public record CartCleared(String cartId, String orderId) {
    }

    // --- External Commands ---

    public record GetCartSnapshotCommand(String commandId, String guestToken, String orderId) implements Command<Void> {
        @Override
        public String getCommandId() {
            return commandId;
        }
    }

    public record GetProductSnapshotsCommand(String commandId, String orderId, List<String> productIds)
            implements Command<Void> {
        @Override
        public String getCommandId() {
            return commandId;
        }
    }

    public record ValidateStockBatchCommand(String commandId, String orderId, Map<String, Integer> items)
            implements Command<Void> {
        @Override
        public String getCommandId() {
            return commandId;
        }
    }

    public record DeductStockForOrderCommand(String commandId, String orderId, Map<String, Integer> items)
            implements Command<Void> {
        @Override
        public String getCommandId() {
            return commandId;
        }
    }

    public record ClearCartCommand(String commandId, String guestToken, String orderId) implements Command<Void> {
        @Override
        public String getCommandId() {
            return commandId;
        }
    }

    public record CreateOrderCommand(
            String commandId,
            String orderId,
            String guestToken,
            CustomerInfo customer,
            ShippingAddress address,
            List<OrderLineItem> items,
            OrderTotals totals,
            String idempotencyKey) implements Command<Void> {
        @Override
        public String getCommandId() {
            return commandId;
        }
    }

    public record MarkCheckoutCompletedCommand(String commandId, String orderId) implements Command<Void> {
        @Override
        public String getCommandId() {
            return commandId;
        }
    }

    // --- Subsystem Specific DTOs (copied locally for decoupling) ---

    public record CustomerInfo(String firstName, String lastName, String email, String phone) {
    }

    public record ShippingAddress(String addressLine1, String addressLine2, String city, String state, String zipCode,
            String country) {
    }

    public record OrderLineItem(String productId, String sku, String productName, BigDecimal unitPrice, int quantity) {
        public BigDecimal getLineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public record OrderTotals(BigDecimal subtotal, BigDecimal shippingFee, BigDecimal tax, BigDecimal total) {
        public static OrderTotals of(BigDecimal subtotal, BigDecimal shippingFee) {
            return new OrderTotals(subtotal, shippingFee, BigDecimal.ZERO, subtotal.add(shippingFee));
        }
    }

    public record OrderCreated(String orderId) {
    } // Local record for event listener parsing
}
