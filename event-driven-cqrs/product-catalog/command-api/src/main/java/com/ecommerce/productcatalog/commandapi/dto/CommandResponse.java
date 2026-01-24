package com.ecommerce.productcatalog.commandapi.dto;

/**
 * Response DTO for command operations.
 */
public class CommandResponse {

    private String trackingId;
    private String status;
    private String message;
    private Object data;

    private CommandResponse(String trackingId, String status, String message, Object data) {
        this.trackingId = trackingId;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static CommandResponse accepted(String trackingId) {
        return new CommandResponse(trackingId, "ACCEPTED", "Command accepted for processing", null);
    }

    public static CommandResponse success(String message, Object data) {
        return new CommandResponse(null, "SUCCESS", message, data);
    }

    public static CommandResponse error(String message) {
        return new CommandResponse(null, "ERROR", message, null);
    }

    public String getTrackingId() {
        return trackingId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
