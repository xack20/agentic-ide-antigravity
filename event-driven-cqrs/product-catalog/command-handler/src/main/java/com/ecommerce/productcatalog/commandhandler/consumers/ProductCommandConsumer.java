package com.ecommerce.productcatalog.commandhandler.consumers;

import com.ecommerce.productcatalog.application.commands.*;
import com.ecommerce.productcatalog.application.handlers.*;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductCommandConsumer.class);

    private final CreateProductCommandHandler createHandler;
    private final UpdateProductDetailsCommandHandler updateHandler;
    private final ChangeProductPriceCommandHandler priceHandler;
    private final ActivateProductCommandHandler activateHandler;
    private final DeactivateProductCommandHandler deactivateHandler;
    private final GetProductSnapshotsCommandHandler snapshotsHandler;
    private final ObjectMapper objectMapper;

    public ProductCommandConsumer(
            CreateProductCommandHandler createHandler,
            UpdateProductDetailsCommandHandler updateHandler,
            ChangeProductPriceCommandHandler priceHandler,
            ActivateProductCommandHandler activateHandler,
            DeactivateProductCommandHandler deactivateHandler,
            GetProductSnapshotsCommandHandler snapshotsHandler,
            ObjectMapper objectMapper) {
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.priceHandler = priceHandler;
        this.activateHandler = activateHandler;
        this.deactivateHandler = deactivateHandler;
        this.snapshotsHandler = snapshotsHandler;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = MessagingConstants.PRODUCT_CATALOG_COMMANDS_QUEUE)
    public void handleCommand(Message message) {
        String commandType = (String) message.getMessageProperties().getHeader(MessagingConstants.HEADER_COMMAND_TYPE);

        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            JsonNode payload = root.get("command");

            switch (commandType) {
                case "CreateProductCommand" ->
                    createHandler.handle(objectMapper.treeToValue(payload, CreateProductCommand.class)).join();
                case "UpdateProductDetailsCommand" ->
                    updateHandler.handle(objectMapper.treeToValue(payload, UpdateProductDetailsCommand.class)).join();
                case "ChangeProductPriceCommand" ->
                    priceHandler.handle(objectMapper.treeToValue(payload, ChangeProductPriceCommand.class)).join();
                case "ActivateProductCommand" ->
                    activateHandler.handle(objectMapper.treeToValue(payload, ActivateProductCommand.class)).join();
                case "DeactivateProductCommand" ->
                    deactivateHandler.handle(objectMapper.treeToValue(payload, DeactivateProductCommand.class)).join();
                case "GetProductSnapshotsCommand" ->
                    snapshotsHandler.handle(objectMapper.treeToValue(payload, GetProductSnapshotsCommand.class)).join();
                default -> logger.warn("Unknown command type: {}", commandType);
            }
        } catch (Exception e) {
            logger.error("Failed to process product command", e);
        }
    }
}
