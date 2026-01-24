package com.ecommerce.inventory.eventhandler.consumers;

import com.ecommerce.inventory.queryapi.models.StockAvailabilityView;
import com.ecommerce.inventory.queryapi.repositories.StockAvailabilityRepository;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final StockAvailabilityRepository repository;
    private final ObjectMapper objectMapper;

    public InventoryEventConsumer(StockAvailabilityRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MessagingConstants.INVENTORY_EVENTS_QUEUE, durable = "true"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "inventory.#"))
    public void handleEvent(Message message) {
        String eventType = (String) message.getMessageProperties().getHeader(MessagingConstants.HEADER_EVENT_TYPE);

        try {
            JsonNode root = objectMapper.readTree(message.getBody());

            logger.info("Received event: type={}", eventType);

            if ("StockSet".equals(eventType)) {
                String productId = root.get("productId").asText();
                int newQty = root.get("newQty").asInt();

                updateView(productId, newQty);
            } else if ("StockDeductedForOrder".equals(eventType)) {
                String productId = root.get("productId").asText();
                int newQty = root.get("newQty").asInt();

                updateView(productId, newQty);
            }
            // StockDeductionRejected likely doesn't verify view updates unless we track
            // denials stats.

        } catch (Exception e) {
            logger.error("Error processing event", e);
        }
    }

    private void updateView(String productId, int newQty) {
        repository.findById(productId)
                .ifPresentOrElse(view -> {
                    view.setAvailableQty(newQty);
                    repository.save(view);
                }, () -> {
                    StockAvailabilityView view = new StockAvailabilityView();
                    view.setProductId(productId);
                    view.setAvailableQty(newQty);
                    repository.save(view);
                });
    }
}
