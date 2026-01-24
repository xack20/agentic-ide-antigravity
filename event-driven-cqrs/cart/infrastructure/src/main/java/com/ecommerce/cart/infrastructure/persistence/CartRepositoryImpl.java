package com.ecommerce.cart.infrastructure.persistence;

import com.ecommerce.cart.application.ports.CartRepository;
import com.ecommerce.cart.domain.aggregates.CartItem;
import com.ecommerce.cart.domain.aggregates.ShoppingCart;
import com.ecommerce.cart.domain.valueobjects.CartId;
import com.ecommerce.cart.domain.valueobjects.GuestToken;
import com.ecommerce.cart.domain.valueobjects.ProductId;
import com.ecommerce.cart.domain.valueobjects.Quantity;
import com.ecommerce.shared.common.persistence.ConcurrencyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class CartRepositoryImpl implements CartRepository {

    private final CartMongoRepository mongoRepository;

    public CartRepositoryImpl(CartMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public CompletableFuture<Optional<ShoppingCart>> findById(CartId id) {
        return CompletableFuture.supplyAsync(() -> mongoRepository.findById(id.getValue()).map(this::toAggregate));
    }

    @Override
    public CompletableFuture<Optional<ShoppingCart>> findByGuestToken(GuestToken guestToken) {
        return CompletableFuture
                .supplyAsync(() -> mongoRepository.findByGuestToken(guestToken.getValue()).map(this::toAggregate));
    }

    @Override
    public CompletableFuture<ShoppingCart> save(ShoppingCart aggregate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CartDocument doc = toDocument(aggregate);
                doc.setUpdatedAt(Instant.now());
                if (doc.getCreatedAt() == null) {
                    doc.setCreatedAt(Instant.now());
                }

                CartDocument saved = mongoRepository.save(doc);
                return toAggregate(saved);
            } catch (OptimisticLockingFailureException ex) {
                throw new ConcurrencyException(
                        aggregate.getId().getValue(),
                        aggregate.getVersion(),
                        -1);
            }
        });
    }

    private ShoppingCart toAggregate(CartDocument doc) {
        Map<ProductId, CartItem> items = new HashMap<>();
        if (doc.getItems() != null) {
            doc.getItems().forEach((k, v) -> {
                ProductId pid = ProductId.of(k);
                items.put(pid, new CartItem(pid, Quantity.of(v)));
            });
        }

        return ShoppingCart.reconstitute(
                CartId.of(doc.getCartId()),
                GuestToken.of(doc.getGuestToken()),
                items,
                doc.getVersion() != null ? doc.getVersion() : 0);
    }

    private CartDocument toDocument(ShoppingCart aggregate) {
        CartDocument doc = new CartDocument();
        doc.setCartId(aggregate.getId().getValue());
        doc.setGuestToken(aggregate.getGuestToken().getValue());

        Map<String, Integer> docItems = new HashMap<>();
        aggregate.getItems().forEach((k, v) -> docItems.put(k.getValue(), v.getQuantity().getValue()));
        doc.setItems(docItems);

        if (!aggregate.isNew()) {
            doc.setVersion(aggregate.getVersion());
        }
        return doc;
    }
}
