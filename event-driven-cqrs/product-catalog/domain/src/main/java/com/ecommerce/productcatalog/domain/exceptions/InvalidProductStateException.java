package com.ecommerce.productcatalog.domain.exceptions;

/**
 * Exception thrown when an invalid state transition is attempted on a product.
 */
public class InvalidProductStateException extends ProductDomainException {

    public InvalidProductStateException(String productId, String currentState, String attemptedAction) {
        super(
                String.format("Cannot %s product %s in state %s", attemptedAction, productId, currentState),
                productId,
                "INVALID_STATE_TRANSITION");
    }
}
