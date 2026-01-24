package com.ecommerce.productcatalog.application.commands;

/**
 * Result of UpdateProductCommand execution.
 */
public class UpdateProductResult {

    private final boolean success;
    private final String errorMessage;

    private UpdateProductResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static UpdateProductResult success() {
        return new UpdateProductResult(true, null);
    }

    public static UpdateProductResult failure(String errorMessage) {
        return new UpdateProductResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
