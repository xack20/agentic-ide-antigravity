package com.ecommerce.productcatalog.domain.aggregates;

import com.ecommerce.productcatalog.domain.events.*;
import com.ecommerce.productcatalog.domain.exceptions.InvalidProductStateException;
import com.ecommerce.productcatalog.domain.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Product aggregate.
 */
class ProductTest {

    @Test
    void create_shouldProduceProductCreatedEvent() {
        // Given
        ProductId id = ProductId.generate();
        ProductName name = ProductName.of("Test Product");
        Money price = Money.usd(99.99);
        String description = "A test product";
        String sku = "TEST-001";

        // When
        Product product = Product.create(id, name, description, price, sku);

        // Then
        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getDescription()).isEqualTo(description);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getSku()).isEqualTo(sku);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
        assertThat(product.getVersion()).isEqualTo(0);

        assertThat(product.getUncommittedEvents()).hasSize(1);
        assertThat(product.getUncommittedEvents().get(0)).isInstanceOf(ProductCreated.class);

        ProductCreated event = (ProductCreated) product.getUncommittedEvents().get(0);
        assertThat(event.getProductId()).isEqualTo(id.getValue());
        assertThat(event.getName()).isEqualTo(name.getValue());
    }

    @Test
    void update_shouldProduceProductUpdatedEvent() {
        // Given
        Product product = createTestProduct();
        product.clearUncommittedEvents();

        ProductName newName = ProductName.of("Updated Product");
        String newDescription = "Updated description";
        Money newPrice = Money.usd(149.99);

        // When
        product.update(newName, newDescription, newPrice);

        // Then
        assertThat(product.getName()).isEqualTo(newName);
        assertThat(product.getDescription()).isEqualTo(newDescription);
        assertThat(product.getPrice()).isEqualTo(newPrice);

        assertThat(product.getUncommittedEvents()).hasSize(1);
        assertThat(product.getUncommittedEvents().get(0)).isInstanceOf(ProductUpdated.class);
    }

    @Test
    void activate_shouldChangeStatusToActive() {
        // Given
        Product product = createTestProduct();
        product.clearUncommittedEvents();

        // When
        product.activate();

        // Then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.isAvailable()).isTrue();

        assertThat(product.getUncommittedEvents()).hasSize(1);
        assertThat(product.getUncommittedEvents().get(0)).isInstanceOf(ProductActivated.class);
    }

    @Test
    void activate_whenAlreadyActive_shouldBeIdempotent() {
        // Given
        Product product = createTestProduct();
        product.activate();
        product.clearUncommittedEvents();

        // When
        product.activate();

        // Then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(product.getUncommittedEvents()).isEmpty();
    }

    @Test
    void deactivate_shouldChangeStatusToInactive() {
        // Given
        Product product = createTestProduct();
        product.activate();
        product.clearUncommittedEvents();

        // When
        product.deactivate();

        // Then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
        assertThat(product.isAvailable()).isFalse();

        assertThat(product.getUncommittedEvents()).hasSize(1);
        assertThat(product.getUncommittedEvents().get(0)).isInstanceOf(ProductDeactivated.class);
    }

    @Test
    void delete_shouldChangeStatusToDeleted() {
        // Given
        Product product = createTestProduct();
        product.clearUncommittedEvents();

        // When
        product.delete();

        // Then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED);

        assertThat(product.getUncommittedEvents()).hasSize(1);
        assertThat(product.getUncommittedEvents().get(0)).isInstanceOf(ProductDeleted.class);
    }

    @Test
    void update_whenDeleted_shouldThrowException() {
        // Given
        Product product = createTestProduct();
        product.delete();
        product.clearUncommittedEvents();

        // When/Then
        assertThatThrownBy(() -> product.update(
                ProductName.of("New Name"),
                "New Description",
                Money.usd(50.0)))
                .isInstanceOf(InvalidProductStateException.class)
                .hasMessageContaining("Cannot update product");
    }

    @Test
    void activate_whenDeleted_shouldThrowException() {
        // Given
        Product product = createTestProduct();
        product.delete();
        product.clearUncommittedEvents();

        // When/Then
        assertThatThrownBy(() -> product.activate())
                .isInstanceOf(InvalidProductStateException.class)
                .hasMessageContaining("Cannot activate product");
    }

    @Test
    void clearUncommittedEvents_shouldRemoveAllEvents() {
        // Given
        Product product = createTestProduct();
        assertThat(product.getUncommittedEvents()).isNotEmpty();

        // When
        product.clearUncommittedEvents();

        // Then
        assertThat(product.getUncommittedEvents()).isEmpty();
    }

    private Product createTestProduct() {
        return Product.create(
                ProductId.generate(),
                ProductName.of("Test Product"),
                "Description",
                Money.usd(99.99),
                "SKU-001");
    }
}
