package com.zest.product.service.impl;

import com.zest.product.dto.request.ProductRequest;
import com.zest.product.dto.response.ProductResponse;
import com.zest.product.entity.Product;
import com.zest.product.exception.ProductNotFoundException;
import com.zest.product.repository.ProductRepository;
import com.zest.product.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        Product product = Product.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .createdBy(getCurrentUsername())
                .createdOn(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {

        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // =========================================================
    // GET PRODUCT BY ID
    // Redis Caching
    // =========================================================

    @Override
    @Cacheable(value = "products", key = "#p0")
    public ProductResponse getProductById(Long id) {


        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return mapToResponse(product);
    }

    // =========================================================
    // UPDATE PRODUCT
    // Updates Redis cache
    // =========================================================

    @Override
    @CachePut(value = "products", key = "#p0")
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        product.setModifiedBy(getCurrentUsername());
        product.setModifiedOn(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // =========================================================
    // DELETE PRODUCT
    // Removes product from Redis cache
    // =========================================================

    @Override
    @CacheEvict(value = "products", key = "#p0")
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(product);
    }

    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================

    @Override
    public Page<ProductResponse> searchProducts(
            String name,
            Pageable pageable) {

        return productRepository
                .findByProductNameContainingIgnoreCase(
                        name,
                        pageable
                )
                .map(this::mapToResponse);
    }

    // =========================================================
    // ENTITY -> RESPONSE DTO
    // =========================================================

    private ProductResponse mapToResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .createdBy(product.getCreatedBy())
                .createdOn(product.getCreatedOn())
                .modifiedBy(product.getModifiedBy())
                .modifiedOn(product.getModifiedOn())
                .build();
    }

    // =========================================================
    // CURRENT LOGGED-IN USER
    // =========================================================

    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }
}