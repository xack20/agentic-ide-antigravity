package com.ecommerce.order.eventhandler.consumers;

import com.ecommerce.order.queryapi.models.AdminOrderListView;
import com.ecommerce.order.queryapi.models.OrderDetailView;
import com.ecommerce.order.queryapi.repositories.AdminOrderListViewRepository;
import com.ecommerce.order.queryapi.repositories.OrderDetailViewRepository;
import com.ecommerce.order.domain.valueobjects.CustomerInfo;
import com.ecommerce.order.domain.valueobjects.OrderTotals;
import com.ecommerce.order.domain.valueobjects.ShippingAddress;
import com.ecommerce.order.domain.aggregates.OrderLineItem;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final OrderDetailViewRepository detailRepository;
    private final AdminOrderListViewRepository listRepository;
    private final ObjectMapper objectMapper;

    public OrderEventConsumer(OrderDetailViewRepository detailRepository, AdminOrderListViewRepository listRepository, ObjectMapper objectMapper) {
        this.detailRepository = detailRepository;
        this.listRepository = listRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessagingConstants.ORDER_EVENTS_QUEUE, durable = "true"),
            exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"),
            key = "order.#"
    ))
    public void handleEvent(Message message) {
        String eventType = (String) message.getMessageProperties().getHeader(MessagingConstants.HEADER_EVENT_TYPE);
        
        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            logger.info("Received event: type={}", eventType);

            if ("OrderCreated".equals(eventType)) {
                String orderId = root.get("orderId").asText();
                String orderNumber = root.get("orderNumber").asText();
                String guestToken = root.get("guestToken").asText();

                // Deserialize complex objects manually or via map
                JsonNode cust = root.get("customer");
                CustomerInfo customer = new CustomerInfo(cust.get("name").asText(), cust.get("phone").asText(), cust.get("email").asText());

                JsonNode addr = root.get("address");
                ShippingAddress address = new ShippingAddress(addr.get("line1").asText(), addr.get("city").asText(), addr.get("postalCode").asText(), addr.get("country").asText());

                JsonNode tot = root.get("totals");
                OrderTotals totals = new OrderTotals(
                        new BigDecimal(tot.get("subtotal").asText()), 
                        new BigDecimal(tot.get("shippingFee").asText()), 
                        new BigDecimal(tot.get("total").asText())
                );

                List<OrderLineItem> items = objectMapper.readValue(
                        root.get("items").traverse(), 
                        new TypeReference<List<OrderLineItem>>() {});
                
                // Save Detail View
                OrderDetailView detail = new OrderDetailView(orderId, orderNumber, guestToken, customer, address, items, totals);
                detailRepository.save(detail);

                // Save List View
                AdminOrderListView list = new AdminOrderListView(orderId, orderNumber, customer.getName(), customer.getPhone(), totals.getTotal(), "Created", detail.getCreatedAt());
                listRepository.save(list);
            }
        } catch (Exception e) {
            logger.error("Error processing order event", e);
        }
    }
}
