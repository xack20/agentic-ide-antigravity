package com.ecommerce.checkoutsaga.handler.saga;

import com.ecommerce.checkoutsaga.handler.saga.contracts.SagaContracts;
import com.ecommerce.checkoutsaga.handler.saga.contracts.SagaContracts.*;
import com.ecommerce.checkout.domain.events.CheckoutRequested;
import com.ecommerce.shared.common.commands.Command;
import com.ecommerce.shared.common.commands.CommandEnvelope;
import com.ecommerce.shared.messaging.MessagingConstants;
import com.ecommerce.shared.messaging.CommandPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Component
public class CheckoutSagaManager {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutSagaManager.class);

    private final CommandPublisher commandPublisher;
    private final ObjectMapper objectMapper;

    // In a real system, we'd persist Saga state to DB (MongoDB/SQL).
    // For MVP, if we stateless-ly chain events, we need to pass context in headers
    // or payload.
    // Here we will try to pass context via the events themselves if possible, or
    // assume stateless execution where
    // each step has enough info.
    // However, the events define the contract.
    // Example: CartSnapshotProvided has orderId.
    // But we need to keep "Customer Info" and "Address" from the start
    // (CheckoutRequested) until CreateOrder.
    // If we don't persist state, we must pass it along.
    // The current events design doesn't carry all that.
    // SO: We MUST persist Saga State.

    // Quick MVP workaround: In-memory map (NOT production ready, will fail on
    // restart).
    // Or we rely on the fact that we can't easily change the event contracts now?
    // Wait, I can update the Saga Manager to just use a simple cache or just ...
    // Let's use a static map for MVP demo.
    private static final Map<String, SagaState> sagaStore = new HashMap<>();

    public CheckoutSagaManager(CommandPublisher commandPublisher, ObjectMapper objectMapper) {
        this.commandPublisher = commandPublisher;
        this.objectMapper = objectMapper;
    }

    // 1. Listen to CheckoutRequested -> Send GetCartSnapshot
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "saga.checkout-requested.queue"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "order.CheckoutRequested"))
    public void onCheckoutRequested(Message message) {
        try {
            CheckoutRequested event = objectMapper.readValue(message.getBody(), CheckoutRequested.class);
            logger.info("Saga Started: CheckoutRequested for orderId={}", event.getOrderId());

            // Save initial state
            SagaState state = new SagaState();
            state.orderId = event.getOrderId();
            state.guestToken = event.getGuestToken();
            // Convert domain event VO to local saga record
            state.customer = new CustomerInfo(
                    event.getCustomer().getFirstName(),
                    event.getCustomer().getLastName(),
                    event.getCustomer().getEmail(),
                    event.getCustomer().getPhone());
            state.address = new ShippingAddress(
                    event.getAddress().getAddressLine1(),
                    event.getAddress().getAddressLine2(),
                    event.getAddress().getCity(),
                    event.getAddress().getState(),
                    event.getAddress().getZipCode(),
                    event.getAddress().getCountry());
            state.idempotencyKey = event.getIdempotencyKey();
            sagaStore.put(event.getOrderId(), state);

            // Command: Get Cart
            GetCartSnapshotCommand cmd = new GetCartSnapshotCommand(UUID.randomUUID().toString(), event.getGuestToken(),
                    event.getOrderId());
            sendCommand(MessagingConstants.CART_COMMANDS_QUEUE, cmd, event.getOrderId());

        } catch (Exception e) {
            logger.error("Error starting saga", e);
        }
    }

    // 2. Listen to CartSnapshotProvided -> Send GetProductSnapshots
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "saga.cart-snapshot.queue"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "cart.CartSnapshotProvided" // Need
                                                                                                                                                                                                                     // to
                                                                                                                                                                                                                     // ensure
                                                                                                                                                                                                                     // Cart
                                                                                                                                                                                                                     // emits
                                                                                                                                                                                                                     // with
                                                                                                                                                                                                                     // this
                                                                                                                                                                                                                     // routing
                                                                                                                                                                                                                     // key?
                                                                                                                                                                                                                     // AggType
                                                                                                                                                                                                                     // is
                                                                                                                                                                                                                     // ShoppingCart.
    ))
    public void onCartSnapshotProvided(Message message) {
        try {
            CartSnapshotProvided event = objectMapper.readValue(message.getBody(), CartSnapshotProvided.class);
            logger.info("Saga Step 2: Cart Snapshot received for orderId={}", event.orderId());

            SagaState state = sagaStore.get(event.orderId());
            if (state == null) {
                logger.warn("Saga state not found for {}", event.orderId());
                return;
            }

            state.cartItems = event.items();

            if (state.cartItems.isEmpty()) {
                logger.error("Cart is empty, aborting saga");
                // Should send failure/compensation?
                return;
            }

            // Command: Get Products
            GetProductSnapshotsCommand cmd = new GetProductSnapshotsCommand(UUID.randomUUID().toString(),
                    event.orderId(), state.cartItems.keySet().stream().toList());
            // We need a PRODUCT_CATALOG_COMMANDS_QUEUE. Check constants.
            sendCommand(MessagingConstants.PRODUCT_CATALOG_COMMANDS_QUEUE, cmd, event.orderId());

        } catch (Exception e) {
            logger.error("Error step 2", e);
        }
    }

    // 3. Listen to ProductSnapshots -> Validate Stock
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "saga.product-snapshots.queue"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "productcatalog.ProductSnapshotsProvided"))
    public void onProductSnapshots(Message message) {
        try {
            ProductSnapshotsProvided event = objectMapper.readValue(message.getBody(), ProductSnapshotsProvided.class);
            logger.info("Saga Step 3: Product Snapshots received for orderId={}", event.orderId());

            SagaState state = sagaStore.get(event.orderId());
            if (state == null)
                return;

            state.products = event.products();

            // Validate product details (active, price check?)
            // For now, store them.
            // Converting event pojo to local or generic
            // Ignoring type conversion for brevity, assuming compatible or just storing
            // logic

            // Command: Validate Stock
            ValidateStockBatchCommand cmd = new ValidateStockBatchCommand(UUID.randomUUID().toString(),
                    event.orderId(), state.cartItems);
            sendCommand(MessagingConstants.INVENTORY_COMMANDS_QUEUE, cmd, event.orderId());

        } catch (Exception e) {
            logger.error("Error step 3", e);
        }
    }

    // 4. Listen to StockBatchValidated -> Deduct Stock
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "saga.stock-validated.queue"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "inventorybatch.StockBatchValidated"))
    public void onStockValidated(Message message) {
        try {
            StockBatchValidated event = objectMapper.readValue(message.getBody(), StockBatchValidated.class);
            logger.info("Saga Step 4: Stock Validated for orderId={}, success={}", event.orderId(),
                    event.success());

            if (!event.success()) {
                logger.error("Stock validation failed: {}", event.failureReason());
                return;
            }

            SagaState state = sagaStore.get(event.orderId());
            if (state == null)
                return;

            // Command: Deduct Stock
            DeductStockForOrderCommand cmd = new DeductStockForOrderCommand(UUID.randomUUID().toString(),
                    event.orderId(), state.cartItems);
            // Re-using same queue as validate?
            sendCommand(MessagingConstants.INVENTORY_COMMANDS_QUEUE, cmd, event.orderId());

        } catch (Exception e) {
            logger.error("Error step 4", e);
        }
    }

    // 5. Listen to StockDeducted -> Create Order
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "saga.stock-deducted.queue"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "inventory.StockDeductedForOrder"))
    public void onStockDeducted(Message message) {
        try {
            StockDeductedForOrder event = objectMapper.readValue(message.getBody(), StockDeductedForOrder.class);
            logger.info("Saga Step 5: Stock Deducted for orderId={}", event.orderId());

            SagaState state = sagaStore.get(event.orderId());
            if (state == null)
                return;

            // map state.products and state.cartItems to local OrderLineItems
            java.util.List<OrderLineItem> lineItems = state.products.stream()
                    .map(p -> {
                        Integer qty = state.cartItems.get(p.id());
                        return new OrderLineItem(
                                p.id(), p.sku(), p.name(), p.price(), qty != null ? qty : 0);
                    }).toList();

            java.math.BigDecimal subtotal = lineItems.stream()
                    .map(OrderLineItem::getLineTotal)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            OrderTotals totals = OrderTotals.of(subtotal, java.math.BigDecimal.ZERO);

            CreateOrderCommand cmd = new CreateOrderCommand(
                    UUID.randomUUID().toString(),
                    event.orderId(),
                    state.guestToken,
                    state.customer,
                    state.address,
                    lineItems,
                    totals,
                    state.idempotencyKey);

            sendCommand(MessagingConstants.ORDER_COMMANDS_QUEUE, cmd, event.orderId());

        } catch (Exception e) {
            logger.error("Error step 5", e);
        }
    }

    // 6. Listen to OrderCreated -> Clear Cart
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "saga.order-created.queue"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "order.OrderCreated"))
    public void onOrderCreated(Message message) {
        try {
            OrderCreated event = objectMapper.readValue(message.getBody(), OrderCreated.class);
            logger.info("Saga Step 6: Order Created for orderId={}", event.orderId());

            SagaState state = sagaStore.get(event.orderId());
            String guestToken = state != null ? state.guestToken : "unknown";

            ClearCartCommand cmd = new ClearCartCommand(UUID.randomUUID().toString(), guestToken, event.orderId());
            sendCommand(MessagingConstants.CART_COMMANDS_QUEUE, cmd, event.orderId());

        } catch (Exception e) {
            logger.error("Error step 6", e);
        }
    }

    // 7. Listen to CartCleared -> Mark Complete
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "saga.cart-cleared.queue"), exchange = @Exchange(value = MessagingConstants.EVENTS_EXCHANGE, type = "topic"), key = "cart.CartCleared"))
    public void onCartCleared(Message message) {
        try {
            CartCleared event = objectMapper.readValue(message.getBody(), CartCleared.class);
            String orderId = event.orderId();

            if (orderId != null) {
                logger.info("Saga Final Step: Marking Checkout Completed for orderId={}", orderId);
                MarkCheckoutCompletedCommand cmd = new MarkCheckoutCompletedCommand(UUID.randomUUID().toString(),
                        orderId);
                sendCommand(MessagingConstants.ORDER_COMMANDS_QUEUE, cmd, orderId);

                // Cleanup Store
                sagaStore.remove(orderId);
            }
        } catch (Exception e) {
            logger.error("Error finalizing saga", e);
        }
    }

    private <T extends Command<?>> void sendCommand(String queue, T commandPayload, String correlationId) {
        CommandEnvelope<T> envelope = new CommandEnvelope.Builder<>(commandPayload)
                .correlationId(correlationId)
                .build();

        commandPublisher.publish(queue, envelope);
    }

    // Simple In-Memory State
    static class SagaState {
        String orderId;
        String guestToken;
        CustomerInfo customer;
        ShippingAddress address;
        String idempotencyKey;
        Map<String, Integer> cartItems;
        java.util.List<ProductSnapshotsProvided.ProductSnapshot> products;
    }
}
