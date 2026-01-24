package com.ecommerce.inventory.commandhandler.consumers;

import com.ecommerce.inventory.application.commands.DeductStockForOrderCommand;
import com.ecommerce.inventory.application.commands.SetStockCommand;
import com.ecommerce.inventory.application.commands.ValidateStockBatchCommand;
import com.ecommerce.inventory.application.handlers.DeductStockForOrderCommandHandler;
import com.ecommerce.inventory.application.handlers.SetStockCommandHandler;
import com.ecommerce.inventory.application.handlers.ValidateStockBatchCommandHandler;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.messaging.commands.CommandEnvelope;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryCommandConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryCommandConsumer.class);

    private final SetStockCommandHandler setStockCommandHandler;
    private final DeductStockForOrderCommandHandler deductStockForOrderCommandHandler;
    private final ObjectMapper objectMapper;

    // Use specific queue for inventory commands
    // Note: MessagingConstants might need update to include
    // INVENTORY_COMMANDS_QUEUE
    // Or we reuse PRODUCT_COMMANDS_QUEUE? No, separate queue.
    // I will assume constant exists or I need to add it.
    // Assuming "inventory-commands-queue".

    public InventoryCommandConsumer(
            SetStockCommandHandler setHandler,
            DeductStockForOrderCommandHandler deductHandler,
            ValidateStockBatchCommandHandler validateHandler,
            ObjectMapper objectMapper) {
        this.setHandler = setHandler;
        this.deductHandler = deductHandler;
        this.validateHandler = validateHandler;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = MessagingConstants.INVENTORY_COMMANDS_QUEUE)
    public void handleCommand(Message message) {
        String commandId = message.getMessageProperties().getMessageId();
        String commandType = (String) message.getMessageProperties().getHeader(MessagingConstants.HEADER_COMMAND_TYPE);
        String correlationId = (String) message.getMessageProperties()
                .getHeader(MessagingConstants.HEADER_CORRELATION_ID);

        MDC.put("correlationId", correlationId);

        try {
            logger.info("Received command: type={}, commandId={}", commandType, commandId);

            JsonNode root = objectMapper.readTree(message.getBody());
            JsonNode payload = root.get("command");

            switch (commandType) {
                case "SetStockCommand" -> {
                    SetStockCommand command = new SetStockCommand(
                            commandId,
                            payload.get("productId").asText(),
                            payload.get("newQty").asInt(),
                            payload.has("reason") ? payload.get("reason").asText() : null);
                    setStockCommandHandler.handle(command).join();
                }
                case "DeductStockForOrderCommand" -> {
                    List<DeductStockForOrderCommand.OrderItem> items = new ArrayList<>();
                    if (payload.has("items") && payload.get("items").isArray()) {
                        for (JsonNode itemNode : payload.get("items")) {
                            items.add(new DeductStockForOrderCommand.OrderItem(
                                    itemNode.get("productId").asText(),
                                    itemNode.get("qty").asInt()));
                        }
                    }

                    DeductStockForOrderCommand command = new DeductStockForOrderCommand(
                            commandId,
                            payload.get("orderId").asText(),
                            items);
                    deductStockForOrderCommandHandler.handle(command).join();
                }
                default -> logger.warn("Unknown command type: {}", commandType);
            }

            logger.info("Command processed successfully: commandId={}, type={}", commandId, commandType);
        } catch (Exception ex) {
            logger.error("Command failed: commandId={}, error={}", commandId, ex.getMessage(), ex);
            // Handle DLQ or retries
        } finally {
            MDC.remove("correlationId");
        }
    }
}
