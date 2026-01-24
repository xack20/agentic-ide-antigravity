package com.ecommerce.productcatalog.application.commands;

/**
 * Result of CreateProductCommand execution.
 */
public class CreateProductResult {

    private final String productId;
    private final boolean success;
    private final String errorMessage;

    private CreateProductResult(String productId, boolean success, String errorMessage) {
        this.productId = productId;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static CreateProductResult success(String productId) {
        return new CreateProductResult(productId, true, null);
    }

    public static CreateProductResult failure(String errorMessage) {
        return new CreateProductResult(null, false, errorMessage);
    }

    public String getProductId() {
        return productId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
