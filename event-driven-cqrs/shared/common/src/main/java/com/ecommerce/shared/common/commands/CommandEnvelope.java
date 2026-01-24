package com.ecommerce.shared.common.commands;

import java.time.Instant;
import java.util.UUID;

/**
 * Envelope wrapping a command with cross-cutting metadata.
 * Used for correlation, tracing, and multi-tenancy.
 *
 * @param <T> The command type being wrapped
 */
public class CommandEnvelope<T extends Command<?>> {

    private final T command;
    private final String correlationId;
    private final String causationId;
    private final String userId;
    private final String tenantId;
    private final Instant timestamp;

    public CommandEnvelope(T command, String correlationId, String causationId,
            String userId, String tenantId) {
        this.command = command;
        this.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        this.causationId = causationId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.timestamp = Instant.now();
    }

    public static <T extends Command<?>> CommandEnvelope<T> wrap(T command) {
        return new CommandEnvelope<>(command, null, null, null, null);
    }

    public static <T extends Command<?>> Builder<T> builder(T command) {
        return new Builder<>(command);
    }

    public T getCommand() {
        return command;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getCausationId() {
        return causationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public static class Builder<T extends Command<?>> {
        private final T command;
        private String correlationId;
        private String causationId;
        private String userId;
        private String tenantId;

        public Builder(T command) {
            this.command = command;
        }

        public Builder<T> correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder<T> causationId(String causationId) {
            this.causationId = causationId;
            return this;
        }

        public Builder<T> userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder<T> tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public CommandEnvelope<T> build() {
            return new CommandEnvelope<>(command, correlationId, causationId, userId, tenantId);
        }
    }
}
