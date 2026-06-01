package com.lld.graph_ecommerce_service.data_fetchers;


import com.lld.graph_ecommerce_service.generated.types.*;
import com.lld.graph_ecommerce_service.services.CartService;
import com.lld.graph_ecommerce_service.services.ProductService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.beans.factory.annotation.Autowired;

@DgsComponent
public class CatalogAndCartDataFetcher {

    @Autowired
    private ProductService productService; // Assuming a typical Spring Service layered architecture

    @Autowired
    private CartService cartService;

    @DgsQuery
    public Cart getCart(@InputArgument String id) {
        return cartService.getCart(id);
    }

    // --- MUTATIONS ---

    @DgsMutation
    public Cart addItemToCart(@InputArgument String customerId, @InputArgument CartItemInput item) {
        return cartService.addItem(customerId, item);
    }

    @DgsMutation
    public Order processCheckout(@InputArgument String customerId, @InputArgument PaymentType paymentMethod) {
        // Complex business logic wrapping transaction, inventory check, and order creation
        return cartService.checkout(customerId, paymentMethod);
    }
}
