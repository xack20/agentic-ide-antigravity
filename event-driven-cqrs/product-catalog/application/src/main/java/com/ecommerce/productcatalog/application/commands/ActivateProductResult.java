package com.ecommerce.productcatalog.application.commands;

/**
 * Result of ActivateProductCommand execution.
 */
public class ActivateProductResult {

    private final boolean success;
    private final String errorMessage;

    private ActivateProductResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ActivateProductResult success() {
        return new ActivateProductResult(true, null);
    }

    public static ActivateProductResult failure(String errorMessage) {
        return new ActivateProductResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
