package com.ecommerce.productcatalog.commandapi.controllers;

import com.ecommerce.productcatalog.application.commands.CreateProductCommand;
import com.ecommerce.productcatalog.commandapi.dto.CommandResponse;
import com.ecommerce.productcatalog.commandapi.dto.CreateProductRequest;
import com.ecommerce.shared.common.commands.CommandEnvelope;
import com.ecommerce.shared.messaging.CommandPublisher;
import com.ecommerce.shared.messaging.MessagingConstants;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for product commands.
 * This is the Command API process - it only validates and enqueues commands.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductCommandController {

    private static final Logger logger = LoggerFactory.getLogger(ProductCommandController.class);

    private final CommandPublisher commandPublisher;

    public ProductCommandController(CommandPublisher commandPublisher) {
        this.commandPublisher = commandPublisher;
    }

    /**
     * Create a new product.
     * Validates request, creates command, publishes to queue, returns 202 Accepted.
     */
    @PostMapping
    public ResponseEntity<CommandResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {

        String commandId = UUID.randomUUID().toString();
        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();

        logger.info("Received CreateProduct request: correlationId={}, sku={}",
                corrId, request.getSku());

        CreateProductCommand command = new CreateProductCommand(
                commandId,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCurrency(),
                request.getSku());

        CommandEnvelope<CreateProductCommand> envelope = CommandEnvelope.builder(command)
                .correlationId(corrId)
                .tenantId(tenantId)
                .build();

        commandPublisher.publish(MessagingConstants.PRODUCT_CATALOG_COMMANDS_QUEUE, envelope)
                .exceptionally(ex -> {
                    logger.error("Failed to publish command: {}", ex.getMessage(), ex);
                    return null;
                });

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(CommandResponse.accepted(commandId));
    }

    /**
     * Activate a product.
     */
    @PostMapping("/{productId}/activate")
    public ResponseEntity<CommandResponse> activateProduct(
            @PathVariable String productId,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {

        String commandId = UUID.randomUUID().toString();
        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();

        logger.info("Received ActivateProduct request: correlationId={}, productId={}",
                corrId, productId);

        // In a full implementation, this would publish ActivateProductCommand
        // For now, return accepted
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(CommandResponse.accepted(commandId));
    }
}
