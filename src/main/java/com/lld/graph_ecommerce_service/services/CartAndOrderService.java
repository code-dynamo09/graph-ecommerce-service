package com.lld.graph_ecommerce_service.services;



import com.lld.graph_ecommerce_service.generated.types.*;
import com.lld.graph_ecommerce_service.models.entities.*;
import com.lld.graph_ecommerce_service.models.entities.FulfillmentStatus;
import com.lld.graph_ecommerce_service.repositories.CartRepository;
import com.lld.graph_ecommerce_service.repositories.OrderRepository;
import com.lld.graph_ecommerce_service.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartAndOrderService {

    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;

    public Cart getCart(String customerId) {
        CartEntity cartEntity = cartRepository.findById(customerId)
                .orElseGet(() -> {
                    CartEntity c = new CartEntity();
                    c.setCustomerId(customerId);
                    return cartRepository.save(c);
                });
        return mapToGraphQLCart(cartEntity);
    }

    public Cart addItemToCart(String customerId, CartItemInput itemInput) {
        CartEntity cart = cartRepository.findById(customerId).orElseGet(() -> {
            CartEntity c = new CartEntity();
            c.setCustomerId(customerId);
            return c;
        });

        ProductEntity product = productRepository.findById(itemInput.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItemEntity item = new CartItemEntity();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(itemInput.getQuantity());
        cart.getItems().add(item);

        return mapToGraphQLCart(cartRepository.save(cart));
    }

    public Order checkout(String customerId, PaymentType paymentType) {
        CartEntity cart = cartRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        double subTotal = cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();

        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus(FulfillmentStatus.PROCESSING);
        order.setPlacedAt(LocalDateTime.now());
        order.setCustomerId(customerId);
        order.setCustomerFullName("John Doe");
        order.setCustomerEmail("john.doe@example.com");
        order.setCustomerTier("GOLD");

        FinancialsEmbeddable financials = new FinancialsEmbeddable(subTotal, subTotal * 0.1, 15.0, (subTotal * 1.1) + 15.0);
        order.setFinancials(financials);
        order.setPaymentType(paymentType.name());
        order.setTransactionToken("TOK-" + UUID.randomUUID().toString().substring(0, 6));

        // Create line items
        order.setItems(cart.getItems().stream().map(cartItem -> {
            OrderLineItemEntity lineItem = new OrderLineItemEntity();
            lineItem.setOrder(order);
            lineItem.setProduct(cartItem.getProduct());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
            return lineItem;
        }).collect(Collectors.toList()));

        orderRepository.save(order);
        cartRepository.delete(cart); // Empty the cart post-checkout

        return mapToGraphQLOrder(order);
    }

    private Cart mapToGraphQLCart(CartEntity entity) {
        double subtotal = entity.getItems().stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();

        return Cart.newBuilder()
                .id(entity.getCustomerId())
                .customerId(entity.getCustomerId())
                .cartSubtotal(subtotal)
                .items(entity.getItems().stream().map(i -> CartItem.newBuilder()
                        .quantity(i.getQuantity())
                        .product(Product.newBuilder().id(i.getProduct().getId()).name(i.getProduct().getName()).price(i.getProduct().getPrice()).build())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    private Order mapToGraphQLOrder(OrderEntity entity) {
        // Determine whether to instantiate CreditCardPayment or WalletPayment for the polymorphic field
        PaymentInstrument payment = "CREDIT_CARD".equals(entity.getPaymentType()) ?
                CreditCardPayment.newBuilder().id(entity.getId()).amount(entity.getFinancials().getGrandTotal()).transactionToken(entity.getTransactionToken()).cardBrand("VISA").maskedPan("************1234").build() :
                WalletPayment.newBuilder().id(entity.getId()).amount(entity.getFinancials().getGrandTotal()).transactionToken(entity.getTransactionToken()).walletProvider("APPLE_PAY").build();

        return Order.newBuilder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .status(entity.getStatus() == FulfillmentStatus.PROCESSING ? com.lld.graph_ecommerce_service.generated.types.FulfillmentStatus.PROCESSING : com.lld.graph_ecommerce_service.generated.types.FulfillmentStatus.PENDING_PAYMENT)
                .placedAt(entity.getPlacedAt().toString())
                .customer(CustomerProfile.newBuilder().id(entity.getCustomerId()).fullName(entity.getCustomerFullName()).email(entity.getCustomerEmail()).tier(LoyaltyTier.valueOf(entity.getCustomerTier())).build())
                .financials(OrderFinancials.newBuilder().subTotal(entity.getFinancials().getSubTotal()).tax(entity.getFinancials().getTax()).shippingFee(entity.getFinancials().getShippingFee()).grandTotal(entity.getFinancials().getGrandTotal()).build())
                .paymentDetails(payment)
                .build();
    }
}