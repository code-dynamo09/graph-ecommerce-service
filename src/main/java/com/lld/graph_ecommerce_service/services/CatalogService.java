package com.lld.graph_ecommerce_service.services;

import com.lld.graph_ecommerce_service.generated.types.*;
import com.lld.graph_ecommerce_service.models.entities.ProductEntity;
import com.lld.graph_ecommerce_service.models.entities.ReviewEntity;
import com.lld.graph_ecommerce_service.repositories.CategoryRepository;
import com.lld.graph_ecommerce_service.repositories.ProductRepository;
import com.lld.graph_ecommerce_service.repositories.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public Product fetchProductById(String id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToGraphQLProduct(entity);
    }

    public List<Category> fetchAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> Category.newBuilder().id(c.getId()).name(c.getName()).slug(c.getSlug()).build())
                .collect(Collectors.toList());
    }

    @Transactional
    public Review addReview(ReviewInput input) {
        ProductEntity product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setProduct(product);
        reviewEntity.setAuthorName(input.getAuthorName());
        reviewEntity.setRating(input.getRating());
        reviewEntity.setHeadline(input.getHeadline());
        reviewEntity.setComment(input.getComment());
        reviewEntity.setCreatedAt(LocalDateTime.now());

        ReviewEntity saved = reviewRepository.save(reviewEntity);
        return mapToGraphQLReview(saved);
    }

    public ProductConnection search(ProductFilterInput filter, Integer limit, Integer offset) {
        int pageNum = (offset != null && limit != null) ? offset / limit : 0;
        int pageSize = limit != null ? limit : 10;

        // Simplified fetch without full JPA criteria specs for demonstration clarity
        Page<ProductEntity> page = productRepository.findAll(PageRequest.of(pageNum, pageSize));

        List<ProductEdge> edges = page.getContent().stream()
                .map(p -> ProductEdge.newBuilder()
                        .cursor(p.getId())
                        .node(mapToGraphQLProduct(p))
                        .build())
                .collect(Collectors.toList());

        PageInfo pageInfo = PageInfo.newBuilder()
                .hasNextPage(page.hasNext())
                .totalCount((int) page.getTotalElements())
                .build();

        return ProductConnection.newBuilder().edges(edges).pageInfo(pageInfo).build();
    }

    private Product mapToGraphQLProduct(ProductEntity entity) {
        double avgRating = entity.getReviews() != null ?
                entity.getReviews().stream().mapToInt(ReviewEntity::getRating).average().orElse(0.0) : 0.0;

        return Product.newBuilder()
                .id(entity.getId())
                .sku(entity.getSku())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .averageRating(avgRating)
                .category(Category.newBuilder().id(entity.getCategory().getId()).name(entity.getCategory().getName()).build())
                .inventory(InventoryDetails.newBuilder()
                        .availableStock(entity.getInventory().getAvailableStock())
                        .warehouseLocation(entity.getInventory().getWarehouseLocation())
                        .isBackorderAllowed(entity.getInventory().getIsBackorderAllowed()).build())
                .reviews(entity.getReviews() != null ? entity.getReviews().stream().map(this::mapToGraphQLReview).collect(Collectors.toList()) : List.of())
                .build();
    }

    private Review mapToGraphQLReview(ReviewEntity entity) {
        return Review.newBuilder()
                .id(entity.getId())
                .authorName(entity.getAuthorName())
                .rating(entity.getRating())
                .headline(entity.getHeadline())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt().toString())
                .build();
    }
}