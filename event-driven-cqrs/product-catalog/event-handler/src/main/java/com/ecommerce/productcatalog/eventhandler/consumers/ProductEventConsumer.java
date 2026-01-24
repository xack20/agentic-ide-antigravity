package com.ecommerce.productcatalog.eventhandler.consumers;

import com.ecommerce.productcatalog.eventhandler.projections.ProductCatalogView;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.persistence.ProcessedEventDocument;
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
import java.time.Instant;

/**
 * Event consumer for ProductCatalog projection updates.
 * Implements idempotency via processed event tracking.
 */
@Component
public class ProductEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ProductEventConsumer.class);
    private static final String PROJECTION_NAME = "ProductCatalogView";

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public ProductEventConsumer(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = MessagingConstants.PRODUCT_CATALOG_EVENTS_QUEUE)
    public void handleEvent(Message message) {
        String eventId = message.getMessageProperties().getMessageId();
        String eventType = (String) message.getMessageProperties()
                .getHeader(MessagingConstants.HEADER_EVENT_TYPE);
        String correlationId = (String) message.getMessageProperties()
                .getHeader(MessagingConstants.HEADER_CORRELATION_ID);

        MDC.put("correlationId", correlationId);

        try {
            logger.info("Received event: type={}, eventId={}", eventType, eventId);

            // Idempotency check
            if (isAlreadyProcessed(eventId)) {
                logger.info("Event already processed, skipping: eventId={}", eventId);
                return;
            }

            JsonNode payload = objectMapper.readTree(message.getBody());

            switch (eventType) {
                case "ProductCreated" -> handleProductCreated(payload);
                case "ProductDetailsUpdated" -> handleProductDetailsUpdated(payload);
                case "ProductPriceChanged" -> handleProductPriceChanged(payload);
                case "ProductActivated" -> handleProductActivated(payload);
                case "ProductDeactivated" -> handleProductDeactivated(payload);
                case "ProductDeleted" -> handleProductDeleted(payload);
                default -> logger.warn("Unknown event type: {}", eventType);
            }

            markAsProcessed(eventId, eventType);
            logger.info("Event processed successfully: eventId={}", eventId);
        } catch (Exception ex) {
            logger.error("Error processing event: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to process event", ex);
        } finally {
            MDC.remove("correlationId");
        }
    }

    private void handleProductCreated(JsonNode payload) {
        ProductCatalogView view = new ProductCatalogView();
        view.setId(payload.get("aggregateId").asText());
        view.setName(payload.get("name").asText());
        view.setDescription(payload.has("description") ? payload.get("description").asText() : null);
        view.setPrice(new BigDecimal(payload.get("price").asText()));
        view.setCurrency(payload.get("currency").asText());
        view.setSku(payload.get("sku").asText());
        view.setStatus(payload.get("status").asText());
        view.setCreatedAt(Instant.now());
        view.setUpdatedAt(Instant.now());

        mongoTemplate.save(view);
        logger.debug("Created projection for product: {}", view.getId());
    }

    private void handleProductDetailsUpdated(JsonNode payload) {
        String productId = payload.get("aggregateId").asText();
        ProductCatalogView view = mongoTemplate.findById(productId, ProductCatalogView.class);

        if (view != null) {
            view.setName(payload.get("name").asText());
            view.setDescription(payload.has("description") ? payload.get("description").asText() : null);
            view.setUpdatedAt(Instant.now());

            mongoTemplate.save(view);
            logger.debug("Updated details projection for product: {}", productId);
        }
    }

    private void handleProductPriceChanged(JsonNode payload) {
        String productId = payload.get("aggregateId").asText();
        ProductCatalogView view = mongoTemplate.findById(productId, ProductCatalogView.class);

        if (view != null) {
            view.setPrice(new BigDecimal(payload.get("newPrice").asText()));
            view.setCurrency(payload.get("currency").asText());
            view.setUpdatedAt(Instant.now());

            mongoTemplate.save(view);
            logger.debug("Updated price projection for product: {}", productId);
        }
    }

    private void handleProductActivated(JsonNode payload) {
        updateStatus(payload.get("aggregateId").asText(), "ACTIVE");
    }

    private void handleProductDeactivated(JsonNode payload) {
        updateStatus(payload.get("aggregateId").asText(), "INACTIVE");
    }

    private void handleProductDeleted(JsonNode payload) {
        updateStatus(payload.get("aggregateId").asText(), "DELETED");
    }

    private void updateStatus(String productId, String status) {
        ProductCatalogView view = mongoTemplate.findById(productId, ProductCatalogView.class);
        if (view != null) {
            view.setStatus(status);
            view.setUpdatedAt(Instant.now());
            mongoTemplate.save(view);
            logger.debug("Updated status for product {} to {}", productId, status);
        }
    }

    private boolean isAlreadyProcessed(String eventId) {
        String id = PROJECTION_NAME + ":" + eventId;
        return mongoTemplate.exists(
                Query.query(Criteria.where("_id").is(id)),
                ProcessedEventDocument.class);
    }

    private void markAsProcessed(String eventId, String eventType) {
        ProcessedEventDocument doc = new ProcessedEventDocument(eventId, PROJECTION_NAME, eventType);
        mongoTemplate.save(doc);
    }
}
