package com.lld.graph_ecommerce_service.repositories;

import com.lld.graph_ecommerce_service.models.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

}
