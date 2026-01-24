package com.ecommerce.productcatalog.domain.exceptions;

/**
 * Base exception for product domain errors.
 */
public class ProductDomainException extends RuntimeException {

    private final String productId;
    private final String errorCode;

    public ProductDomainException(String message, String productId, String errorCode) {
        super(message);
        this.productId = productId;
        this.errorCode = errorCode;
    }

    public String getProductId() {
        return productId;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
