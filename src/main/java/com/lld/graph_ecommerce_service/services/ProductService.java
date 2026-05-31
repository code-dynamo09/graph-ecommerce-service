package com.lld.graph_ecommerce_service.services;

import com.example.generated.types.*;
import com.lld.graph_ecommerce_service.models.entities.ProductEntity;
import com.lld.graph_ecommerce_service.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Fetches a single product by ID and maps it to the GraphQL type.
     */
    public Product getProductById(String id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        return mapToGraphQLProduct(entity);
    }

    /**
     * Handles searching with Relay-style pagination.
     */
    public ProductConnection search(ProductFilterInput filter, Integer limit, Integer offset) {
        // Handle pagination defaults
        int pageSize = (limit != null && limit > 0) ? limit : 10;
        int pageNum = (offset != null && offset >= 0) ? (offset / pageSize) : 0;

        // Fetch paginated data from the database
        Page<ProductEntity> productPage = productRepository.findAll(PageRequest.of(pageNum, pageSize));

        // 1. Map to Edges (The nodes wrapped with cursors)
        List<ProductEdge> edges = productPage.getContent().stream()
                .map(entity -> {
                    ProductEdge edge = new ProductEdge();
                    edge.setCursor(entity.getId()); // Usually base64 encoded in production
                    edge.setNode(mapToGraphQLProduct(entity));
                    return edge;
                })
                .collect(Collectors.toList());

        // 2. Create PageInfo for the UI to know if there is more data
        PageInfo pageInfo = new PageInfo();
        pageInfo.setHasNextPage(productPage.hasNext());
        pageInfo.setTotalCount((int) productPage.getTotalElements());

        // 3. Construct and return the final Connection object
        ProductConnection connection = new ProductConnection();
        connection.setEdges(edges);
        connection.setPageInfo(pageInfo);

        return connection;
    }

    /**
     * Helper method to map a JPA Database Entity to the DGS GraphQL Schema Type.
     */
    private Product mapToGraphQLProduct(ProductEntity entity) {
        Product gqlProduct = new Product();
        gqlProduct.setId(entity.getId());
        gqlProduct.setName(entity.getName());
        gqlProduct.setPrice(entity.getPrice());
        // Map other fields as necessary (Category, Inventory, etc.)

        if (entity.getCategory() != null) {
            Category category = new Category();
            category.setId(entity.getCategory().getId());
            category.setName(entity.getCategory().getName());
            gqlProduct.setCategory(category);
        }

        return gqlProduct;
    }
}