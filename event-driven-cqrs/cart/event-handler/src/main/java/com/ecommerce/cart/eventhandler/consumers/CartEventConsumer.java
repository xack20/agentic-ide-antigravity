package com.ecommerce.cart.eventhandler.consumers;

import com.ecommerce.cart.queryapi.models.CartView;
import com.ecommerce.cart.queryapi.repositories.CartViewRepository;
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

import java.util.HashMap;

@Component
public class CartEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CartEventConsumer.class);

    private final CartViewRepository repository;
    private final ObjectMapper objectMapper;

    public CartEventConsumer(CartViewRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MessagingConstants.CART_EVENTS_QUEUE, durable = "true"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "cart.#"))
    public void handleEvent(Message message) {
        String eventType = (String) message.getMessageProperties().getHeader(MessagingConstants.HEADER_EVENT_TYPE);

        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            logger.info("Received event: type={}", eventType);

            switch (eventType) {
                case "CartCreated" -> {
                    String cartId = root.get("cartId").asText();
                    String guestToken = root.get("guestToken").asText();

                    CartView view = new CartView(cartId, guestToken, new HashMap<>());
                    repository.save(view);
                }
                case "CartItemAdded" -> {
                    String cartId = root.get("cartId").asText();
                    String productId = root.get("productId").asText();
                    int qty = root.get("qty").asInt();

                    repository.findById(cartId).ifPresent(view -> {
                        view.getItems().merge(productId, qty, Integer::sum);
                        repository.save(view);
                    });
                }
                case "CartItemQuantityUpdated" -> {
                    String cartId = root.get("cartId").asText();
                    String productId = root.get("productId").asText();
                    int newQty = root.get("newQty").asInt();

                    repository.findById(cartId).ifPresent(view -> {
                        view.getItems().put(productId, newQty);
                        repository.save(view);
                    });
                }
                case "CartItemRemoved" -> {
                    String cartId = root.get("cartId").asText();
                    String productId = root.get("productId").asText();

                    repository.findById(cartId).ifPresent(view -> {
                        view.getItems().remove(productId);
                        repository.save(view);
                    });
                }
                case "CartCleared" -> {
                    String cartId = root.get("cartId").asText();

                    repository.findById(cartId).ifPresent(view -> {
                        view.getItems().clear();
                        repository.save(view);
                    });
                }
                default -> logger.info("Ignored cart event: {}", eventType);
            }

        } catch (Exception e) {
            logger.error("Error processing cart event", e);
        }
    }
}
