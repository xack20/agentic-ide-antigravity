package com.ecommerce.productcatalog.commandhandler.consumers;

import com.ecommerce.productcatalog.application.commands.*;
import com.ecommerce.productcatalog.application.handlers.*;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.persistence.ProcessedCommandDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * RabbitMQ consumer for ProductCatalog commands.
 * Implements idempotency via processed command tracking.
 */
@Component
public class ProductCommandConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductCommandConsumer.class);
    private static final String HANDLER_TYPE = "ProductCommandHandler";

    private final CreateProductCommandHandler createProductCommandHandler;
    private final UpdateProductDetailsCommandHandler updateProductDetailsCommandHandler;
    private final ChangeProductPriceCommandHandler changeProductPriceCommandHandler;
    private final ActivateProductCommandHandler activateProductCommandHandler;
    private final DeactivateProductCommandHandler deactivateProductCommandHandler;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public ProductCommandConsumer(
            CreateProductCommandHandler createProductCommandHandler,
            UpdateProductDetailsCommandHandler updateProductDetailsCommandHandler,
            ChangeProductPriceCommandHandler changeProductPriceCommandHandler,
            ActivateProductCommandHandler activateProductCommandHandler,
            DeactivateProductCommandHandler deactivateProductCommandHandler,
            MongoTemplate mongoTemplate,
            ObjectMapper objectMapper) {
        this.createProductCommandHandler = createProductCommandHandler;
        this.updateProductDetailsCommandHandler = updateProductDetailsCommandHandler;
        this.changeProductPriceCommandHandler = changeProductPriceCommandHandler;
        this.activateProductCommandHandler = activateProductCommandHandler;
        this.deactivateProductCommandHandler = deactivateProductCommandHandler;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = MessagingConstants.PRODUCT_CATALOG_COMMANDS_QUEUE)
    public void handleCommand(Message message) {
        String correlationId = null;

        try {
            // Parse the envelope
            JsonNode root = objectMapper.readTree(message.getBody());
            correlationId = root.has("correlationId") ? root.get("correlationId").asText() : null;
            MDC.put("correlationId", correlationId);

            JsonNode commandNode = root.get("command");
            if (commandNode == null) {
                logger.error("No command field in message");
                return;
            }

            String commandId = commandNode.get("commandId").asText();
            String commandType = root.has("commandType") ? root.get("commandType").asText() : "CreateProductCommand";

            logger.info("Received command: type={}, commandId={}", commandType, commandId);

            // Idempotency check
            if (isAlreadyProcessed(commandId)) {
                logger.info("Command already processed, skipping: commandId={}", commandId);
                return;
            }

            boolean success = false;
            String result = null;
            String parsingError = null;

            try {
                if ("CreateProductCommand".equals(commandType)) {
                    CreateProductCommand cmd = new CreateProductCommand(
                            commandId,
                            commandNode.get("name").asText(),
                            commandNode.has("description") ? commandNode.get("description").asText() : null,
                            commandNode.get("price").decimalValue(),
                            commandNode.get("currency").asText(),
                            commandNode.get("sku").asText());

                    var res = createProductCommandHandler.handle(cmd).join();
                    if (res.isSuccess()) {
                        success = true;
                        result = res.getProductId();
                    } else {
                        parsingError = res.getErrorMessage();
                    }
                } else if ("UpdateProductDetailsCommand".equals(commandType)) {
                    UpdateProductDetailsCommand cmd = new UpdateProductDetailsCommand(
                            commandId,
                            commandNode.get("productId").asText(),
                            commandNode.get("name").asText(),
                            commandNode.get("description").asText());

                    updateProductDetailsCommandHandler.handle(cmd).join();
                    success = true;
                    result = "Updated";
                } else if ("ChangeProductPriceCommand".equals(commandType)) {
                    ChangeProductPriceCommand cmd = new ChangeProductPriceCommand(
                            commandId,
                            commandNode.get("productId").asText(),
                            commandNode.get("newPrice").decimalValue(),
                            commandNode.get("currency").asText());

                    changeProductPriceCommandHandler.handle(cmd).join();
                    success = true;
                    result = "PriceChanged";
                } else if ("ActivateProductCommand".equals(commandType)) {
                    ActivateProductCommand cmd = new ActivateProductCommand(
                            commandId,
                            commandNode.get("productId").asText());

                    activateProductCommandHandler.handle(cmd).join();
                    success = true;
                    result = "Activated";
                } else if ("DeactivateProductCommand".equals(commandType)) {
                    DeactivateProductCommand cmd = new DeactivateProductCommand(
                            commandId,
                            commandNode.get("productId").asText());

                    deactivateProductCommandHandler.handle(cmd).join();
                    success = true;
                    result = "Deactivated";
                } else {
                    logger.error("Unknown command type: {}", commandType);
                    return;
                }
            } catch (Exception e) {
                logger.error("Handler execution failed for commandId={}: {}", commandId, e.getMessage(), e);
                parsingError = e.getMessage();
            }

            if (success) {
                markAsProcessed(commandId, commandType, result);
                logger.info("Command processed successfully: commandId={}, type={}", commandId, commandType);
            } else {
                logger.warn("Command failed: commandId={}, error={}", commandId, parsingError);
            }
        } catch (Exception ex) {
            logger.error("Error processing command: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to process command", ex);
        } finally {
            MDC.remove("correlationId");
        }
    }

    private boolean isAlreadyProcessed(String commandId) {
        String id = HANDLER_TYPE + ":" + commandId;
        return mongoTemplate.exists(
                Query.query(Criteria.where("_id").is(id)),
                ProcessedCommandDocument.class);
    }

    private void markAsProcessed(String commandId, String commandType, String result) {
        ProcessedCommandDocument doc = new ProcessedCommandDocument(commandId, HANDLER_TYPE, commandType);
        doc.setResult(result);
        mongoTemplate.save(doc);
    }
}
