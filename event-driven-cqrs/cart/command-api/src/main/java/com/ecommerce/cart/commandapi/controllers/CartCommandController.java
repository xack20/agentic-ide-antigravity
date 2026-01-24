package com.ecommerce.cart.commandapi.controllers;

import com.ecommerce.cart.application.commands.*;
import com.ecommerce.cart.commandapi.dto.AddCartItemRequest;
import com.ecommerce.cart.commandapi.dto.UpdateCartItemRequest;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.messaging.commands.CommandEnvelope;
import com.ecommerce.shared.messaging.commands.CommandPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
public class CartCommandController {

    private final CommandPublisher commandPublisher;

    public CartCommandController(CommandPublisher commandPublisher) {
        this.commandPublisher = commandPublisher;
    }

    @PostMapping("/{guestToken}/items")
    public ResponseEntity<CommandEnvelope<AddCartItemCommand>> addItem(
            @PathVariable String guestToken,
            @RequestBody AddCartItemRequest request) {

        AddCartItemCommand command = new AddCartItemCommand(
                UUID.randomUUID().toString(),
                guestToken,
                request.productId(),
                request.qty());

        CommandEnvelope<AddCartItemCommand> envelope = new CommandEnvelope.Builder<>(command)
                .correlationId(UUID.randomUUID().toString())
                .build();

        commandPublisher.publish(
                MessagingConstants.COMMANDS_EXCHANGE,
                MessagingConstants.CART_COMMANDS_QUEUE,
                envelope);

        return ResponseEntity.accepted().body(envelope);
    }

    @PutMapping("/{guestToken}/items/{productId}")
    public ResponseEntity<CommandEnvelope<UpdateCartItemQtyCommand>> updateItemQty(
            @PathVariable String guestToken,
            @PathVariable String productId,
            @RequestBody UpdateCartItemRequest request) {

        UpdateCartItemQtyCommand command = new UpdateCartItemQtyCommand(
                UUID.randomUUID().toString(),
                guestToken,
                productId,
                request.qty());

        CommandEnvelope<UpdateCartItemQtyCommand> envelope = new CommandEnvelope.Builder<>(command)
                .correlationId(UUID.randomUUID().toString())
                .build();

        commandPublisher.publish(
                MessagingConstants.COMMANDS_EXCHANGE,
                MessagingConstants.CART_COMMANDS_QUEUE,
                envelope);

        return ResponseEntity.accepted().body(envelope);
    }

    @DeleteMapping("/{guestToken}/items/{productId}")
    public ResponseEntity<CommandEnvelope<RemoveCartItemCommand>> removeItem(
            @PathVariable String guestToken,
            @PathVariable String productId) {

        RemoveCartItemCommand command = new RemoveCartItemCommand(
                UUID.randomUUID().toString(),
                guestToken,
                productId);

        CommandEnvelope<RemoveCartItemCommand> envelope = new CommandEnvelope.Builder<>(command)
                .correlationId(UUID.randomUUID().toString())
                .build();

        commandPublisher.publish(
                MessagingConstants.COMMANDS_EXCHANGE,
                MessagingConstants.CART_COMMANDS_QUEUE,
                envelope);

        return ResponseEntity.accepted().body(envelope);
    }

    @DeleteMapping("/{guestToken}")
    public ResponseEntity<CommandEnvelope<ClearCartCommand>> clearCart(
            @PathVariable String guestToken) {

        ClearCartCommand command = new ClearCartCommand(
                UUID.randomUUID().toString(),
                guestToken);

        CommandEnvelope<ClearCartCommand> envelope = new CommandEnvelope.Builder<>(command)
                .correlationId(UUID.randomUUID().toString())
                .build();

        commandPublisher.publish(
                MessagingConstants.COMMANDS_EXCHANGE,
                MessagingConstants.CART_COMMANDS_QUEUE,
                envelope);

        return ResponseEntity.accepted().body(envelope);
    }
}
