package com.ecommerce.checkout.commandapi.controllers;

import com.ecommerce.checkout.application.commands.PlaceOrderCommand;
import com.ecommerce.checkout.commandapi.dto.PlaceOrderRequest;
import com.ecommerce.checkout.domain.events.CustomerInfo;
import com.ecommerce.checkout.domain.events.ShippingAddress;
import com.ecommerce.shared.common.commands.CommandEnvelope;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.messaging.CommandPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutCommandController {

        private final CommandPublisher commandPublisher;

        public CheckoutCommandController(CommandPublisher commandPublisher) {
                this.commandPublisher = commandPublisher;
        }

        @PostMapping
        public ResponseEntity<CommandEnvelope<PlaceOrderCommand>> placeOrder(@RequestBody PlaceOrderRequest request) {
                PlaceOrderCommand command = new PlaceOrderCommand(
                                UUID.randomUUID().toString(),
                                request.guestToken(),
                                new CustomerInfo(
                                                request.customer().firstName(),
                                                request.customer().lastName(),
                                                request.customer().phone(),
                                                request.customer().email()),
                                new ShippingAddress(
                                                request.address().line1(),
                                                request.address().line2(),
                                                request.address().city(),
                                                request.address().state(),
                                                request.address().zipCode(),
                                                request.address().country()),
                                request.idempotencyKey());

                CommandEnvelope<PlaceOrderCommand> envelope = new CommandEnvelope.Builder<>(command)
                                .correlationId(UUID.randomUUID().toString())
                                .build();

                // Checkout commands go to their Own Queue
                // We'll use the CART_COMMANDS_QUEUE for now or define a CHECKOUT_COMMANDS_QUEUE
                // in MessagingConstants
                // Since I can't easily change MessagingConstants now without recompiling
                // shared,
                // I'll check what's available.
                commandPublisher.publish("checkout.commands.queue", envelope);

                return ResponseEntity.accepted().body(envelope);
        }
}
