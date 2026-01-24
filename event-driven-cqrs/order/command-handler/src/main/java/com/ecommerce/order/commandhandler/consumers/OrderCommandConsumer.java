package com.ecommerce.order.commandhandler.consumers;

import com.ecommerce.order.application.commands.PlaceOrderCommand;
import com.ecommerce.order.application.commands.CreateOrderCommand;
import com.ecommerce.order.application.commands.MarkCheckoutCompletedCommand;
import com.ecommerce.order.application.handlers.PlaceOrderCommandHandler;
import com.ecommerce.order.application.handlers.CreateOrderCommandHandler;
import com.ecommerce.order.application.handlers.MarkCheckoutCompletedCommandHandler;
import com.ecommerce.order.domain.valueobjects.CustomerInfo;
import com.ecommerce.order.domain.valueobjects.ShippingAddress;
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
public class OrderCommandConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderCommandConsumer.class);

    private final PlaceOrderCommandHandler placeOrderHandler;
    private final CreateOrderCommandHandler createOrderHandler;
    private final MarkCheckoutCompletedCommandHandler markCompleteHandler;
    private final ObjectMapper objectMapper;

    public OrderCommandConsumer(
            PlaceOrderCommandHandler placeOrderHandler,
            CreateOrderCommandHandler createOrderHandler,
            MarkCheckoutCompletedCommandHandler markCompleteHandler,
            ObjectMapper objectMapper) {
        this.placeOrderHandler = placeOrderHandler;
        this.createOrderHandler = createOrderHandler;
        this.markCompleteHandler = markCompleteHandler;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = MessagingConstants.ORDER_COMMANDS_QUEUE)
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
                case "PlaceOrderCommand" -> {
                    JsonNode customer = payload.get("customer");
                    JsonNode address = payload.get("address");

                    PlaceOrderCommand cmd = new PlaceOrderCommand(
                            commandId,
                            payload.get("guestToken").asText(),
                            new CustomerInfo(
                                    customer.get("name").asText(),
                                    customer.get("phone").asText(),
                                    customer.get("email").asText()),
                            new ShippingAddress(
                                    address.get("line1").asText(),
                                    address.get("city").asText(),
                                    address.get("postalCode").asText(),
                                    address.get("country").asText()),
                            payload.get("idempotencyKey").asText());
                    placeOrderHandler.handle(cmd).join();
                }
                case "CreateOrderCommand" -> {
                    CreateOrderCommand cmd = objectMapper.treeToValue(payload, CreateOrderCommand.class);
                    // We need a handler for this. I'll add it to the consumer if I don't want to
                    // inject more handlers,
                    // or just inject the new handler.
                    // For now, let's assume we inject handlers.
                    createOrderHandler.handle(cmd).join();
                }
                case "MarkCheckoutCompletedCommand" -> {
                    MarkCheckoutCompletedCommand cmd = objectMapper.treeToValue(payload,
                            MarkCheckoutCompletedCommand.class);
                    markCompleteHandler.handle(cmd).join();
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
