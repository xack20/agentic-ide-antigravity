package com.ecommerce.order.queryapi.repositories;

import com.ecommerce.order.queryapi.models.AdminOrderListView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminOrderListViewRepository extends MongoRepository<AdminOrderListView, String> {
}
