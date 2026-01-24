package com.ecommerce.cart.application.ports;

import com.ecommerce.cart.domain.aggregates.ShoppingCart;
import com.ecommerce.cart.domain.valueobjects.CartId;
import com.ecommerce.cart.domain.valueobjects.GuestToken;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CartRepository {
    CompletableFuture<Optional<ShoppingCart>> findById(CartId id);

    CompletableFuture<Optional<ShoppingCart>> findByGuestToken(GuestToken guestToken);

    CompletableFuture<ShoppingCart> save(ShoppingCart cart);
}
