package com.lld.graph_ecommerce_service.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_line_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderLineItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double priceAtPurchase;
}
