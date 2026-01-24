package com.ecommerce.cart.queryapi.controllers;

import com.ecommerce.cart.queryapi.models.CartView;
import com.ecommerce.cart.queryapi.repositories.CartViewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
public class CartQueryController {

    private final CartViewRepository repository;

    public CartQueryController(CartViewRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{guestToken}")
    public ResponseEntity<CartView> getCart(@PathVariable String guestToken) {
        return repository.findByGuestToken(guestToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
