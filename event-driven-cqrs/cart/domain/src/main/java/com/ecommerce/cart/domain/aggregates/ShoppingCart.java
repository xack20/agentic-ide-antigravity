package com.ecommerce.cart.domain.aggregates;

import com.ecommerce.cart.domain.events.*;
import com.ecommerce.cart.domain.valueobjects.*;
import com.ecommerce.shared.common.domain.AggregateRoot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart extends AggregateRoot<CartId> {

    private CartId id;
    private GuestToken guestToken;
    private Map<ProductId, CartItem> items = new HashMap<>();
    private int version;
    private boolean isNew = false; // For persistence optimization

    // Private constructor for reconstitution/creation
    private ShoppingCart() {
    }

    public static ShoppingCart create(CartId cartId, GuestToken guestToken) {
        ShoppingCart cart = new ShoppingCart();
        cart.id = cartId;
        cart.guestToken = guestToken;
        cart.version = 0; // Starts at 0, updated by infra on save
        cart.isNew = true; // Flag to tell repo this is a new insert

        cart.raiseEvent(new CartCreated(cartId.getValue(), guestToken.getValue()));
        return cart;
    }

    public static ShoppingCart reconstitute(CartId id, GuestToken guestToken, Map<ProductId, CartItem> items,
            int version) {
        ShoppingCart cart = new ShoppingCart();
        cart.id = id;
        cart.guestToken = guestToken;
        cart.items = new HashMap<>(items);
        cart.version = version;
        cart.isNew = false;
        return cart;
    }

    public void addItem(ProductId productId, Quantity qty) {
        CartItem existing = items.get(productId);
        int currentQty = existing != null ? existing.getQuantity().getValue() : 0;
        int newTotal = currentQty + qty.getValue();

        Quantity finalQty = Quantity.of(newTotal);

        items.put(productId, new CartItem(productId, finalQty));

        raiseEvent(new CartItemAdded(id.getValue(), productId.getValue(), qty.getValue()));
    }

    public void updateItemQty(ProductId productId, Quantity newQty) {
        CartItem existing = items.get(productId);
        if (existing == null) {
            // Cannot update non-existent item.
            // Domain rule: Throw exception or ignore?
            // Throwing is safer for invariants.
            throw new IllegalArgumentException("Item not found in cart");
        }

        int oldQty = existing.getQuantity().getValue();
        items.put(productId, new CartItem(productId, newQty));

        raiseEvent(new CartItemQuantityUpdated(id.getValue(), productId.getValue(), oldQty, newQty.getValue()));
    }

    public void removeItem(ProductId productId) {
        if (items.remove(productId) != null) {
            raiseEvent(new CartItemRemoved(id.getValue(), productId.getValue()));
        }
    }

    public void clearCart(String orderId) {
        this.items.clear();
        this.version++;
        raiseEvent(new CartCleared(this.id.getValue(), orderId));
    }

    @Override
    public CartId getId() {
        return id;
    }

    public GuestToken getGuestToken() {
        return guestToken;
    }

    public Map<ProductId, CartItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    protected void setVersion(int version) {
        this.version = version;
    }

    public boolean isNew() {
        return isNew;
    }
}
