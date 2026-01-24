package com.ecommerce.order.commandapi.controllers;

import com.ecommerce.shared.messaging.CommandPublisher;
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

        // Order Management endpoints (e.g., manual corrections, staff updates) could go
        // here
        // Checkout flow now starts at /api/v1/checkout
}
