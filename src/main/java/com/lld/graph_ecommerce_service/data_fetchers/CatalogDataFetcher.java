package com.lld.graph_ecommerce_service.data_fetchers;

import com.example.generated.types.Category;
import com.example.generated.types.Product;
import com.example.generated.types.ProductConnection;
import com.example.generated.types.ProductFilterInput;
import com.lld.graph_ecommerce_service.services.CatalogService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DgsComponent
public class CatalogDataFetcher {

    @Autowired
    private CatalogService catalogService;

    @DgsQuery
    public Product viewProductCatalog(@InputArgument String id) {
        // We use the generated Product type directly
        return catalogService.fetchProductById(id);
    }

    @DgsQuery
    public ProductConnection searchProducts(
            @InputArgument ProductFilterInput filter,
            @InputArgument Integer limit,
            @InputArgument Integer offset) {

        return catalogService.search(filter, limit, offset);
    }

    @DgsQuery
    public List<Category> getAllCategories() {
        return catalogService.fetchAllCategories();
    }
}