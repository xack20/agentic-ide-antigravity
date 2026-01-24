package com.ecommerce.inventory.queryapi.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stock_availability_view")
public class StockAvailabilityView {

    @Id
    private String productId;
    private int availableQty;
    private boolean inStock;

    public StockAvailabilityView() {
    }

    public StockAvailabilityView(String productId, int availableQty, boolean inStock) {
        this.productId = productId;
        this.availableQty = availableQty;
        this.inStock = inStock;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
        this.inStock = availableQty > 0;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
