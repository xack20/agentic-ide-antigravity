package com.ecommerce.shared.common.commands;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for processing a specific command type.
 *
 * @param <TCommand>  The command type this handler processes
 * @param <TResponse> The response type returned after processing
 */
public interface CommandHandler<TCommand extends Command<TResponse>, TResponse> {

    /**
     * Handle the command and return the result.
     *
     * @param command The command to process
     * @return A CompletableFuture containing the result
     */
    CompletableFuture<TResponse> handle(TCommand command);

    /**
     * Get the class type of command this handler processes.
     */
    Class<TCommand> getCommandType();
}
