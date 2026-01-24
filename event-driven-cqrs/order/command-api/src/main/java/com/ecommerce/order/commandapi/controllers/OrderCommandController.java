package com.ecommerce.order.commandapi.controllers;

import com.ecommerce.order.application.commands.PlaceOrderCommand;
import com.ecommerce.order.commandapi.dto.PlaceOrderRequest;
import com.ecommerce.order.domain.valueobjects.CustomerInfo;
import com.ecommerce.order.domain.valueobjects.ShippingAddress;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.messaging.commands.CommandEnvelope;
import com.ecommerce.shared.messaging.commands.CommandPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderCommandController {

    private final CommandPublisher commandPublisher;

    public OrderCommandController(CommandPublisher commandPublisher) {
        this.commandPublisher = commandPublisher;
    }

    @PostMapping
    public ResponseEntity<CommandEnvelope<PlaceOrderCommand>> placeOrder(@RequestBody PlaceOrderRequest request) {
        PlaceOrderCommand command = new PlaceOrderCommand(
                UUID.randomUUID().toString(),
                request.guestToken(),
                new CustomerInfo(
                        request.customer().name(),
                        request.customer().phone(),
                        request.customer().email()),
                new ShippingAddress(
                        request.address().line1(),
                        request.address().city(),
                        request.address().postalCode(),
                        request.address().country()),
                request.idempotencyKey());

        CommandEnvelope<PlaceOrderCommand> envelope = new CommandEnvelope.Builder<>(command)
                .correlationId(UUID.randomUUID().toString())
                .build();

        commandPublisher.publish(
                MessagingConstants.COMMANDS_EXCHANGE,
                MessagingConstants.ORDER_COMMANDS_QUEUE,
                envelope);

        return ResponseEntity.accepted().body(envelope);
    }
}
