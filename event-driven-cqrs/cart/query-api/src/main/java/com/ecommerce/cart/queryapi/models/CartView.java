package com.ecommerce.cart.queryapi.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "cart_views")
public class CartView {
    @Id
    private String cartId;

    @Indexed(unique = true)
    private String guestToken;

    private Map<String, Integer> items;

    public CartView() {
    }

    public CartView(String cartId, String guestToken, Map<String, Integer> items) {
        this.cartId = cartId;
        this.guestToken = guestToken;
        this.items = items;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public void setGuestToken(String guestToken) {
        this.guestToken = guestToken;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }
}
