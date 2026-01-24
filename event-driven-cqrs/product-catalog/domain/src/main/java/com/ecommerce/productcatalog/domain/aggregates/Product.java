package com.ecommerce.productcatalog.domain.aggregates;

import com.ecommerce.productcatalog.domain.events.*;
import com.ecommerce.productcatalog.domain.exceptions.InvalidProductStateException;
import com.ecommerce.productcatalog.domain.valueobjects.*;
import com.ecommerce.shared.common.domain.AggregateRoot;

import java.math.BigDecimal;

import java.math.BigDecimal;

/**
 * Product aggregate root.
 * Encapsulates all product-related business logic and invariants.
 */
public class Product extends AggregateRoot<ProductId> {

    private ProductId id;
    private ProductName name;
    private ProductDescription description;
    private Money price;
    private Sku sku;
    private ProductStatus status;
    private int version;
    private boolean isNew = false;

    // Private constructor for controlled creation
    private Product() {
    }

    /**
     * Factory method to create a new product.
     */
    public static Product create(ProductId id, ProductName name, ProductDescription description,
            Money price, Sku sku) {
        Product product = new Product();
        product.id = id;
        product.name = name;
        product.description = description;
        product.price = price;
        product.sku = sku;
        product.status = ProductStatus.DRAFT;
        product.version = 0;
        product.isNew = true;

        // Raise creation event
        product.raiseEvent(new ProductCreated(
                id.getValue(),
                name.getValue(),
                description.getValue(),
                price.getAmount(),
                price.getCurrencyCode(),
                sku.getValue(),
                ProductStatus.DRAFT.name()));

        return product;
    }

    /**
     * Reconstitute a product from persisted state.
     * Used by repository when loading from database.
     */
    public static Product reconstitute(ProductId id, ProductName name, String description,
            Money price, String sku, ProductStatus status, int version) {
        Product product = new Product();
        product.id = id;
        product.name = name;
        product.description = ProductDescription.of(description);
        product.price = price;
        product.sku = Sku.of(sku);
        product.status = status;
        product.version = version;
        return product;
    }

    /**
     * Update product details (Name and Description).
     */
    public void updateDetails(ProductName newName, ProductDescription newDescription) {
        validateNotDeleted("update details");

        this.name = newName;
        this.description = newDescription;

        raiseEvent(new ProductDetailsUpdated(
                id.getValue(),
                newName.getValue(),
                newDescription.getValue()));
    }

    /**
     * Change product price.
     */
    public void changePrice(Money newPrice) {
        validateNotDeleted("change price");

        // Validate price logic (e.g. non-negative is handled by Money VO)

        BigDecimal oldAmount = this.price.getAmount();
        this.price = newPrice;

        raiseEvent(new ProductPriceChanged(
                id.getValue(),
                oldAmount,
                newPrice.getAmount(),
                newPrice.getCurrencyCode()));
    }

    /**
     * Activate the product, making it available for sale.
     */
    public void activate() {
        validateNotDeleted("activate");

        if (status == ProductStatus.ACTIVE) {
            return; // Already active, idempotent
        }

        this.status = ProductStatus.ACTIVE;
        raiseEvent(new ProductActivated(id.getValue()));
    }

    /**
     * Deactivate the product, removing it from sale.
     */
    public void deactivate() {
        validateNotDeleted("deactivate");

        if (status == ProductStatus.INACTIVE || status == ProductStatus.DRAFT) {
            return; // Already inactive, idempotent
        }

        this.status = ProductStatus.INACTIVE;
        raiseEvent(new ProductDeactivated(id.getValue()));
    }

    /**
     * Delete the product (soft delete).
     */
    public void delete() {
        if (status == ProductStatus.DELETED) {
            return; // Already deleted, idempotent
        }

        this.status = ProductStatus.DELETED;
        raiseEvent(new ProductDeleted(id.getValue()));
    }

    /**
     * Check if product is available for sale.
     */
    public boolean isAvailable() {
        return status == ProductStatus.ACTIVE;
    }

    public boolean isNew() {
        return isNew;
    }

    private void validateNotDeleted(String action) {
        if (status == ProductStatus.DELETED) {
            throw new InvalidProductStateException(id.getValue(), status.name(), action);
        }
    }

    // Getters
    @Override
    public ProductId getId() {
        return id;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    protected void setVersion(int version) {
        this.version = version;
    }

    public ProductName getName() {
        return name;
    }

    public String getDescription() {
        return description.getValue();
    }

    public Money getPrice() {
        return price;
    }

    public String getSku() {
        return sku.getValue();
    }

    public ProductStatus getStatus() {
        return status;
    }
}
