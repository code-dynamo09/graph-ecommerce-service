package com.lld.graph_ecommerce_service.models.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialsEmbeddable {
    private Double subTotal;
    private Double tax;
    private Double shippingFee;
    private Double grandTotal;
}
