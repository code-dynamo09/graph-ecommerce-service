package com.lld.graph_ecommerce_service.services;


import com.lld.graph_ecommerce_service.generated.types.*;
import com.lld.graph_ecommerce_service.models.entities.CartEntity;
import com.lld.graph_ecommerce_service.models.entities.CartItemEntity;
import com.lld.graph_ecommerce_service.models.entities.OrderEntity;
import com.lld.graph_ecommerce_service.models.entities.ProductEntity;
import com.lld.graph_ecommerce_service.repositories.CartRepository;
import com.lld.graph_ecommerce_service.repositories.OrderRepository;
import com.lld.graph_ecommerce_service.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Adds an item to the user's cart. Creates a cart if one doesn't exist.
     */
    @Transactional
    public Cart addItem(String customerId, CartItemInput itemInput) {
        // Find existing cart or create a new one
        CartEntity cart = cartRepository.findById(customerId).orElseGet(() -> {
            CartEntity newCart = new CartEntity();
            newCart.setCustomerId(customerId);
            return newCart;
        });

        // Validate product exists
        ProductEntity product = productRepository.findById(itemInput.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Create and add the cart item
        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(itemInput.getQuantity());

        cart.getItems().add(cartItem);

        // Save to DB and return mapped GraphQL object
        CartEntity savedCart = cartRepository.save(cart);
        return mapToGraphQLCart(savedCart);
    }

    /**
     * Converts a Cart into an Order and processes the payment logic.
     */
    @Transactional
    public Order checkout(String customerId, PaymentType paymentMethod) {
        // 1. Retrieve the cart
        CartEntity cart = cartRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cannot checkout: Cart is empty or does not exist."));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout: No items in cart.");
        }

        // 2. Create the Order entity based on cart contents
        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomerId(customerId);
        order.setPaymentType(paymentMethod.name());

        // (In a real app, you would calculate totals, check inventory, and call a payment gateway here)

        // 3. Save the order
        OrderEntity savedOrder = orderRepository.save(order);

        // 4. Clear the cart post-checkout
        cartRepository.delete(cart);

        // 5. Return mapped GraphQL object
        return mapToGraphQLOrder(savedOrder);
    }

    /**
     * Helper to map DB Cart Entity to GraphQL Cart
     */
    private Cart mapToGraphQLCart(CartEntity entity) {
        Cart gqlCart = new Cart();
        gqlCart.setId(entity.getCustomerId());
        gqlCart.setCustomerId(entity.getCustomerId());

        // Map items and calculate subtotal
        double subtotal = 0.0;
        if (entity.getItems() != null) {
            gqlCart.setItems(entity.getItems().stream().map(itemEntity -> {
                CartItem gqlItem = new CartItem();
                gqlItem.setQuantity(itemEntity.getQuantity());

                Product p = new Product();
                p.setId(itemEntity.getProduct().getId());
                p.setName(itemEntity.getProduct().getName());
                p.setPrice(itemEntity.getProduct().getPrice());
                gqlItem.setProduct(p);

                return gqlItem;
            }).collect(Collectors.toList()));

            subtotal = entity.getItems().stream()
                    .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                    .sum();
        }
        gqlCart.setCartSubtotal(subtotal);

        return gqlCart;
    }

    /**
     * Helper to map DB Order Entity to GraphQL Order
     */
    private Order mapToGraphQLOrder(OrderEntity entity) {
        Order gqlOrder = new Order();
        gqlOrder.setId(entity.getId());
        gqlOrder.setOrderNumber(entity.getOrderNumber());
        // Map remaining fields...
        return gqlOrder;
    }
}
