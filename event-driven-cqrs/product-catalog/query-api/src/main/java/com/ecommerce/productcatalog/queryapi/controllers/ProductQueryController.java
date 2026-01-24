package com.ecommerce.productcatalog.queryapi.controllers;

import com.ecommerce.productcatalog.queryapi.models.ProductReadModel;
import com.ecommerce.productcatalog.queryapi.repositories.ProductReadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for product queries (read-only).
 * This is the Query API process - it only reads from the read database.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductQueryController {

    private static final Logger logger = LoggerFactory.getLogger(ProductQueryController.class);

    private final ProductReadRepository productReadRepository;

    public ProductQueryController(ProductReadRepository productReadRepository) {
        this.productReadRepository = productReadRepository;
    }

    /**
     * Get all products with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<ProductReadModel>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        logger.debug("Getting all products: page={}, size={}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<ProductReadModel> products = productReadRepository.findAll(pageRequest);

        return ResponseEntity.ok(products);
    }

    /**
     * Get a product by ID.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductReadModel> getProductById(@PathVariable String productId) {
        logger.debug("Getting product by ID: {}", productId);

        return productReadRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get a product by SKU.
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductReadModel> getProductBySku(@PathVariable String sku) {
        logger.debug("Getting product by SKU: {}", sku);

        return productReadRepository.findBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get products by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProductReadModel>> getProductsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.debug("Getting products by status: {}", status);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductReadModel> products = productReadRepository.findByStatus(
                status.toUpperCase(), pageRequest);

        return ResponseEntity.ok(products);
    }

    /**
     * Search products by name.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductReadModel>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.debug("Searching products: q={}", q);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProductReadModel> products = productReadRepository.findByNameContainingIgnoreCase(
                q, pageRequest);

        return ResponseEntity.ok(products);
    }

    /**
     * Get only active products (available for sale).
     */
    @GetMapping("/active")
    public ResponseEntity<List<ProductReadModel>> getActiveProducts() {
        logger.debug("Getting active products");

        List<ProductReadModel> products = productReadRepository.findByStatusIn(List.of("ACTIVE"));
        return ResponseEntity.ok(products);
    }
}
