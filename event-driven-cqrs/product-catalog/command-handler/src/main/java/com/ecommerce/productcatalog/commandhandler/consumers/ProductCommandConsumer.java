package com.ecommerce.productcatalog.commandhandler.consumers;

import com.ecommerce.productcatalog.application.commands.CreateProductCommand;
import com.ecommerce.productcatalog.application.handlers.CreateProductCommandHandler;
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
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public ProductCommandConsumer(CreateProductCommandHandler createProductCommandHandler,
            MongoTemplate mongoTemplate,
            ObjectMapper objectMapper) {
        this.createProductCommandHandler = createProductCommandHandler;
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

            logger.info("Received command: commandId={}", commandId);

            // Idempotency check
            if (isAlreadyProcessed(commandId)) {
                logger.info("Command already processed, skipping: commandId={}", commandId);
                return;
            }

            // Extract CreateProductCommand fields
            CreateProductCommand createCmd = new CreateProductCommand(
                    commandId,
                    commandNode.get("name").asText(),
                    commandNode.has("description") ? commandNode.get("description").asText() : null,
                    commandNode.get("price").decimalValue(),
                    commandNode.get("currency").asText(),
                    commandNode.get("sku").asText());

            var result = createProductCommandHandler.handle(createCmd).join();

            if (result.isSuccess()) {
                markAsProcessed(commandId, "CreateProductCommand", result.getProductId());
                logger.info("Command processed successfully: commandId={}, productId={}",
                        commandId, result.getProductId());
            } else {
                logger.warn("Command failed: commandId={}, error={}",
                        commandId, result.getErrorMessage());
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
