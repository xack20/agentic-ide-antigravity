package com.ecommerce.inventory.commandapi.controllers;

import com.ecommerce.inventory.commandapi.dto.DeductStockRequest;
import com.ecommerce.inventory.commandapi.dto.SetStockRequest;
import com.ecommerce.inventory.application.commands.DeductStockForOrderCommand;
import com.ecommerce.inventory.application.commands.SetStockCommand;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.messaging.commands.CommandEnvelope;
import com.ecommerce.shared.messaging.commands.CommandPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryCommandController {

    private final CommandPublisher commandPublisher;

    public InventoryCommandController(CommandPublisher commandPublisher) {
        this.commandPublisher = commandPublisher;
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<CommandEnvelope<SetStockCommand>> setStock(
            @PathVariable String productId,
            @RequestBody SetStockRequest request) {

        SetStockCommand command = new SetStockCommand(
                UUID.randomUUID().toString(),
                productId,
                request.newQty(),
                request.reason());

        CommandEnvelope<SetStockCommand> envelope = new CommandEnvelope.Builder<>(command)
                .correlationId(UUID.randomUUID().toString())
                .build();

        // Send to Inventory Queue
        commandPublisher.publish(
                MessagingConstants.COMMANDS_EXCHANGE,
                MessagingConstants.INVENTORY_COMMANDS_QUEUE,
                envelope);

        return ResponseEntity.accepted().body(envelope);
    }

    @PostMapping("/orders/{orderId}/deduct")
    public ResponseEntity<CommandEnvelope<DeductStockForOrderCommand>> deductForOrder(
            @PathVariable String orderId,
            @RequestBody DeductStockRequest request) {

        List<DeductStockForOrderCommand.OrderItem> orderItems = new ArrayList<>();
        if (request.items() != null) {
            for (DeductStockRequest.DeductionItem item : request.items()) {
                orderItems.add(new DeductStockForOrderCommand.OrderItem(item.productId(), item.qty()));
            }
        }

        DeductStockForOrderCommand command = new DeductStockForOrderCommand(
                UUID.randomUUID().toString(),
                orderId,
                orderItems);

        CommandEnvelope<DeductStockForOrderCommand> envelope = new CommandEnvelope.Builder<>(command)
                .correlationId(UUID.randomUUID().toString())
                .build();

        commandPublisher.publish(
                MessagingConstants.COMMANDS_EXCHANGE,
                MessagingConstants.INVENTORY_COMMANDS_QUEUE,
                envelope);

        return ResponseEntity.accepted().body(envelope);
    }
}
