package com.ecommerce.order.queryapi.controllers;

import com.ecommerce.order.queryapi.models.AdminOrderListView;
import com.ecommerce.order.queryapi.models.OrderDetailView;
import com.ecommerce.order.queryapi.repositories.AdminOrderListViewRepository;
import com.ecommerce.order.queryapi.repositories.OrderDetailViewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderQueryController {

    private final OrderDetailViewRepository detailRepository;
    private final AdminOrderListViewRepository listRepository;

    public OrderQueryController(OrderDetailViewRepository detailRepository,
            AdminOrderListViewRepository listRepository) {
        this.detailRepository = detailRepository;
        this.listRepository = listRepository;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailView> getOrder(@PathVariable String orderId) {
        return detailRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin")
    public List<AdminOrderListView> getAllOrders() {
        return listRepository.findAll();
    }
}
