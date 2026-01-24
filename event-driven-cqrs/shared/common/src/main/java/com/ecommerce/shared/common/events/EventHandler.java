package com.ecommerce.shared.common.events;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for handling domain events.
 * Event handlers are used for projections and integration.
 *
 * @param <TEvent> The event type this handler processes
 */
public interface EventHandler<TEvent> {

    /**
     * Handle the event.
     *
     * @param event The event to process
     * @return CompletableFuture that completes when handling is done
     */
    CompletableFuture<Void> handle(TEvent event);

    /**
     * Get the class type of event this handler processes.
     */
    Class<TEvent> getEventType();
}
