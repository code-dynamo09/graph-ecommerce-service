package com.lld.graph_ecommerce_service.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private Integer availableStock;

    @Column(nullable = false)
    private String warehouseLocation;

    @Column(nullable = false)
    private Boolean isBackorderAllowed;
}