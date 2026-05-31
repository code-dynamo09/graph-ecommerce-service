package com.lld.graph_ecommerce_service.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FulfillmentStatus status;

    @Column(nullable = false)
    private LocalDateTime placedAt;

    // Simplified customer representation inside Order context
    private String customerId;
    private String customerFullName;
    private String customerEmail;
    private String customerTier;

    // Embedded shipping details
    @Embedded
    private AddressEmbeddable shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderLineItemEntity> items;

    @Embedded
    private FinancialsEmbeddable financials;

    // Polymorphic Payment info persisted flatly
    private String paymentType; // "CREDIT_CARD" or "DIGITAL_WALLET"
    private String transactionToken;
    private Double paymentAmount;
    private String cardBrandOrWalletProvider;
    private String maskedPan;
}

