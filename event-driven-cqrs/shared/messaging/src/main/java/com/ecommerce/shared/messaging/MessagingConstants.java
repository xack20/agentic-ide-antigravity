package com.ecommerce.shared.messaging;

/**
 * Configuration constants for RabbitMQ exchanges and queues.
 */
public final class MessagingConstants {

    private MessagingConstants() {
    }

    // Exchange names
    public static final String EVENTS_EXCHANGE = "ecommerce.events";
    public static final String COMMANDS_EXCHANGE = "ecommerce.commands";

    // Dead letter
    public static final String DEAD_LETTER_EXCHANGE = "ecommerce.dead-letter";
    public static final String DEAD_LETTER_QUEUE = "ecommerce.dead-letter.queue";

    // Headers
    public static final String HEADER_EVENT_TYPE = "eventType";
    public static final String HEADER_AGGREGATE_TYPE = "aggregateType";
    public static final String HEADER_CORRELATION_ID = "correlationId";
    public static final String HEADER_CAUSATION_ID = "causationId";
    public static final String HEADER_TENANT_ID = "tenantId";

    // Subsystem command queues
    public static final String PRODUCT_CATALOG_COMMANDS_QUEUE = "product-catalog.commands";
    public static final String INVENTORY_COMMANDS_QUEUE = "inventory.commands";
    public static final String CART_COMMANDS_QUEUE = "cart.commands";
    public static final String CHECKOUT_COMMANDS_QUEUE = "checkout.commands";
    public static final String ORDER_COMMANDS_QUEUE = "order-management.commands";
    public static final String PAYMENT_COMMANDS_QUEUE = "payment.commands";

    // Event handler queues (per subsystem)
    public static final String PRODUCT_CATALOG_EVENTS_QUEUE = "product-catalog.events";
    public static final String INVENTORY_EVENTS_QUEUE = "inventory.events";
    public static final String SAGA_EVENTS_QUEUE = "saga.events";
}
