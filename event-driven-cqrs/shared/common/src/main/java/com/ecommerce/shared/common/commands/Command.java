package com.ecommerce.shared.common.commands;

/**
 * Marker interface for all commands.
 * Commands represent intent to change the system state.
 *
 * @param <TResponse> The type of response expected from command execution
 */
public interface Command<TResponse> {

    /**
     * Unique identifier for this command instance.
     * Used for idempotency checks.
     */
    String getCommandId();
}
