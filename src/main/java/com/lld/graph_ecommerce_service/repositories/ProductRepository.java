package com.lld.graph_ecommerce_service.repositories;

import com.lld.graph_ecommerce_service.models.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String>
        , JpaSpecificationExecutor<ProductEntity> {
}
