package com.zest.product.service.impl;

import com.zest.product.dto.request.ProductRequest;
import com.zest.product.dto.response.ProductResponse;
import com.zest.product.entity.Product;
import com.zest.product.exception.ProductNotFoundException;
import com.zest.product.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest request;

    @BeforeEach
    void setUp() {

        // Mock logged-in user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null
                )
        );

        // Request object
        request = ProductRequest.builder()
                .productName("Laptop")
                .description("Gaming Laptop")
                .price(new BigDecimal("50000"))
                .quantity(10)
                .build();

        // Existing product
        product = Product.builder()
                .id(1L)
                .productName("Laptop")
                .description("Gaming Laptop")
                .price(new BigDecimal("50000"))
                .quantity(10)
                .createdBy("admin")
                .build();
    }

    // =========================================================
    // CREATE PRODUCT TEST
    // =========================================================

    @Test
    void createProduct_shouldCreateProduct() {

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response =
                productService.createProduct(request);

        assertNotNull(response);

        assertEquals(1L, response.getId());

        assertEquals(
                "Laptop",
                response.getProductName()
        );

        assertEquals(
                "admin",
                response.getCreatedBy()
        );

        verify(productRepository, times(1))
                .save(any(Product.class));
    }

    // =========================================================
    // GET PRODUCT BY ID TEST
    // =========================================================

    @Test
    void getProductById_shouldReturnProduct() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                productService.getProductById(1L);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Laptop",
                response.getProductName()
        );

        verify(productRepository, times(1))
                .findById(1L);
    }

    // =========================================================
    // GET PRODUCT BY ID - NOT FOUND
    // =========================================================

    @Test
    void getProductById_shouldThrowExceptionWhenNotFound() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(1L)
        );

        verify(productRepository, times(1))
                .findById(1L);
    }

    // =========================================================
    // GET ALL PRODUCTS TEST
    // =========================================================

    @Test
    void getAllProducts_shouldReturnProducts() {

        PageRequest pageable =
                PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(
                        List.of(product)
                );

        when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        Page<ProductResponse> response =
                productService.getAllProducts(pageable);

        assertNotNull(response);

        assertEquals(
                1,
                response.getTotalElements()
        );

        assertEquals(
                "Laptop",
                response.getContent()
                        .get(0)
                        .getProductName()
        );

        verify(productRepository, times(1))
                .findAll(pageable);
    }

    // =========================================================
    // UPDATE PRODUCT TEST
    // =========================================================

    @Test
    void updateProduct_shouldUpdateProduct() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponse response =
                productService.updateProduct(
                        1L,
                        request
                );

        assertNotNull(response);

        assertEquals(
                "Laptop",
                response.getProductName()
        );

        assertEquals(
                "admin",
                response.getModifiedBy()
        );

        verify(productRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .save(product);
    }

    // =========================================================
    // DELETE PRODUCT TEST
    // =========================================================

    @Test
    void deleteProduct_shouldDeleteProduct() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1))
                .findById(1L);

        verify(productRepository, times(1))
                .delete(product);
    }

    // =========================================================
    // DELETE PRODUCT - NOT FOUND
    // =========================================================

    @Test
    void deleteProduct_shouldThrowExceptionWhenNotFound() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(1L)
        );

        verify(productRepository, never())
                .delete(any(Product.class));
    }

    // =========================================================
    // CLEAN SECURITY CONTEXT
    // =========================================================

    @org.junit.jupiter.api.AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }
}