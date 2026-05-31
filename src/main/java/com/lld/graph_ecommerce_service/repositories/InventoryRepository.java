package com.lld.graph_ecommerce_service.repositories;

import com.lld.graph_ecommerce_service.models.entities.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, String> {
}
