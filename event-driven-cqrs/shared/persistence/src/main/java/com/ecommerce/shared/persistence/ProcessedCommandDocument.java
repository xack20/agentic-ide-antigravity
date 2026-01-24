package com.ecommerce.shared.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document for tracking processed commands (idempotency).
 */
@Document(collection = "processed_commands")
@CompoundIndex(name = "command_lookup", def = "{'commandId': 1, 'handlerType': 1}", unique = true)
public class ProcessedCommandDocument {

    @Id
    private String id;

    private String commandId;
    private String handlerType;
    private String commandType;
    private Instant processedAt;
    private String result;

    public ProcessedCommandDocument() {
    }

    public ProcessedCommandDocument(String commandId, String handlerType, String commandType) {
        this.id = handlerType + ":" + commandId;
        this.commandId = commandId;
        this.handlerType = handlerType;
        this.commandType = commandType;
        this.processedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
