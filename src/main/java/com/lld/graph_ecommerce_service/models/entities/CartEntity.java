package com.lld.graph_ecommerce_service.models.entities;
=
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity {
    @Id
    private String customerId; // Use customerId as primary key for 1-to-1 mapping representation

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItemEntity> items = new ArrayList<>();
}
