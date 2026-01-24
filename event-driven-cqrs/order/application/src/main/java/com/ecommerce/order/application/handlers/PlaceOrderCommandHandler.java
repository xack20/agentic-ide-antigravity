package com.ecommerce.order.application.handlers;

import com.ecommerce.order.application.commands.PlaceOrderCommand;
import com.ecommerce.order.application.ports.CartService;
import com.ecommerce.order.application.ports.InventoryService;
import com.ecommerce.order.application.ports.OrderRepository;
import com.ecommerce.order.application.ports.ProductService;
import com.ecommerce.order.domain.aggregates.Order;
import com.ecommerce.order.domain.aggregates.OrderLineItem;
import com.ecommerce.order.domain.valueobjects.IdempotencyKey;
import com.ecommerce.order.domain.valueobjects.OrderId;
import com.ecommerce.order.domain.valueobjects.OrderNumber;
import com.ecommerce.order.domain.valueobjects.OrderTotals;
import com.ecommerce.shared.common.commands.CommandHandler;
import com.ecommerce.shared.common.domain.DomainEvent;
import com.ecommerce.shared.common.events.EventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class PlaceOrderCommandHandler implements CommandHandler<PlaceOrderCommand, Void> {

    private final OrderRepository repository;
    private final CartService cartService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final EventPublisher eventPublisher;

    public PlaceOrderCommandHandler(
            OrderRepository repository,
            CartService cartService,
            ProductService productService,
            InventoryService inventoryService,
            EventPublisher eventPublisher) {
        this.repository = repository;
        this.cartService = cartService;
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<Void> handle(PlaceOrderCommand command) {
        IdempotencyKey idempotencyKey = IdempotencyKey.of(command.getIdempotencyKey());

        // 1. Idempotency Check
        return repository.findByIdempotencyKey(idempotencyKey)
                .thenCompose(optOrder -> {
                    if (optOrder.isPresent()) {
                        // Idempotent success
                        return CompletableFuture.completedFuture(null);
                    }
                    return processOrder(command, idempotencyKey);
                });
    }

    private CompletableFuture<Void> processOrder(PlaceOrderCommand command, IdempotencyKey idempotencyKey) {
        // 2. Fetch Cart Items
        return cartService.getCartItems(command.getGuestToken())
                .thenCompose(cartItems -> {
                    if (cartItems.isEmpty()) {
                        throw new IllegalArgumentException("Cart is empty");
                    }

                    // 3. Fetch Product Metadata (parallel would be better, doing serial for
                    // simplicity/MVP)
                    // TODO: Optimize to fetchAll in parallel
                    List<CompletableFuture<OrderLineItem>> itemFutures = new ArrayList<>();

                    for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
                        String productId = entry.getKey();
                        int qty = entry.getValue();

                        // Check Stock
                        CompletableFuture<OrderLineItem> itemFuture = inventoryService.checkStock(productId, qty)
                                .thenCompose(hasStock -> {
                                    if (!hasStock) {
                                        throw new IllegalArgumentException(
                                                "Insufficient stock for product " + productId);
                                    }
                                    return productService.getProduct(productId);
                                })
                                .thenApply(product -> {
                                    if (!product.active()) {
                                        throw new IllegalArgumentException("Product " + productId + " is inactive");
                                    }
                                    return new OrderLineItem(
                                            product.id(),
                                            product.sku(),
                                            product.name(),
                                            product.price(),
                                            qty);
                                });
                        itemFutures.add(itemFuture);
                    }

                    return CompletableFuture.allOf(itemFutures.toArray(new CompletableFuture[0]))
                            .thenApply(v -> itemFutures.stream().map(CompletableFuture::join).toList());
                })
                .thenCompose(lineItems -> {
                    // 4. Calculate Totals
                    BigDecimal subtotal = lineItems.stream()
                            .map(OrderLineItem::getLineTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    OrderTotals totals = OrderTotals.of(subtotal, BigDecimal.ZERO); // Free shipping

                    // 5. Create Order
                    Order order = Order.create(
                            OrderId.generate(),
                            OrderNumber.generate(),
                            command.getGuestToken(),
                            command.getCustomer(),
                            command.getAddress(),
                            lineItems,
                            totals,
                            idempotencyKey);

                    // 6. Save
                    return repository.save(order);
                })
                .thenAccept(saved -> {
                    // 7. Publish Events
                    List<DomainEvent> events = saved.getUncommittedEvents();
                    for (DomainEvent event : events) {
                        eventPublisher.publish(event);
                    }
                    saved.clearUncommittedEvents();
                });
    }

    @Override
    public Class<PlaceOrderCommand> getCommandType() {
        return PlaceOrderCommand.class;
    }
}
