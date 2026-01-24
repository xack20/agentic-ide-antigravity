package com.ecommerce.shared.messaging;

import com.ecommerce.shared.common.commands.Command;
import com.ecommerce.shared.common.commands.CommandEnvelope;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for publishing commands to RabbitMQ command queues.
 */
public interface CommandPublisher {

    /**
     * Publish a command to its designated queue.
     *
     * @param queueName The target command queue
     * @param envelope  The command envelope with metadata
     * @return CompletableFuture that completes when publish is acknowledged
     */
    <T extends Command<?>> CompletableFuture<Void> publish(String queueName, CommandEnvelope<T> envelope);
}
