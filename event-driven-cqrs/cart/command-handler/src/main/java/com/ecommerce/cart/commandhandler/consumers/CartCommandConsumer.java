package com.ecommerce.cart.commandhandler.consumers;

import com.ecommerce.cart.application.commands.*;
import com.ecommerce.cart.application.handlers.*;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CartCommandConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CartCommandConsumer.class);

    private final AddCartItemCommandHandler addHandler;
    private final UpdateCartItemQtyCommandHandler updateHandler;
    private final RemoveCartItemCommandHandler removeHandler;
    private final ClearCartCommandHAndler clearHandler;
    private final ObjectMapper objectMapper;

    public CartCommandConsumer(
            AddCartItemCommandHandler addHandler,
            UpdateCartItemQtyCommandHandler updateHandler,
            RemoveCartItemCommandHandler removeHandler,
            ClearCartCommandHAndler clearHandler,
            ObjectMapper objectMapper) {
        this.addHandler = addHandler;
        this.updateHandler = updateHandler;
        this.removeHandler = removeHandler;
        this.clearHandler = clearHandler;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = MessagingConstants.CART_COMMANDS_QUEUE)
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
                case "AddCartItemCommand" -> {
                    AddCartItemCommand cmd = new AddCartItemCommand(
                            commandId,
                            payload.get("guestToken").asText(),
                            payload.get("productId").asText(),
                            payload.get("qty").asInt());
                    addHandler.handle(cmd).join();
                }
                case "UpdateCartItemQtyCommand" -> {
                    UpdateCartItemQtyCommand cmd = new UpdateCartItemQtyCommand(
                            commandId,
                            payload.get("guestToken").asText(),
                            payload.get("productId").asText(),
                            payload.get("qty").asInt());
                    updateHandler.handle(cmd).join();
                }
                case "RemoveCartItemCommand" -> {
                    RemoveCartItemCommand cmd = new RemoveCartItemCommand(
                            commandId,
                            payload.get("guestToken").asText(),
                            payload.get("productId").asText());
                    removeHandler.handle(cmd).join();
                }
                case "ClearCartCommand" -> {
                    ClearCartCommand cmd = new ClearCartCommand(
                            commandId,
                            payload.get("guestToken").asText());
                    clearHandler.handle(cmd).join();
                }
                default -> logger.warn("Unknown command type: {}", commandType);
            }

            logger.info("Command processed successfully: commandId={}", commandId);
        } catch (Exception ex) {
            logger.error("Command failed: commandId={}, error={}", commandId, ex.getMessage(), ex);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
