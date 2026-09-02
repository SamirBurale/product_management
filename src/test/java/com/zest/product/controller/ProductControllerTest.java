package com.zest.product.controller;

import com.zest.product.dto.request.ProductRequest;
import com.zest.product.dto.response.ProductResponse;
import com.zest.product.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductResponse productResponse;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {

        // Mock ProductRequest
        productRequest = mock(ProductRequest.class);

        // Sample ProductResponse
        productResponse = ProductResponse.builder()
                .id(15L)
                .productName("Gaming Laptop")
                .description("High performance gaming laptop")
                .price(new BigDecimal("75000"))
                .quantity(10)
                .createdBy("admin")
                .createdOn(LocalDateTime.now())
                .modifiedBy(null)
                .modifiedOn(null)
                .build();
    }

    // =========================================================
    // TEST CREATE PRODUCT
    // =========================================================

    @Test
    void createProduct_ShouldReturnCreatedProduct() {

        when(productService.createProduct(productRequest))
                .thenReturn(productResponse);

        ResponseEntity<ProductResponse> response =
                productController.createProduct(productRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(15L, response.getBody().getId());

        assertEquals(
                "Gaming Laptop",
                response.getBody().getProductName()
        );

        verify(productService)
                .createProduct(productRequest);
    }

    // =========================================================
    // TEST SEARCH PRODUCTS
    // =========================================================

    @Test
    void searchProducts_ShouldReturnProducts() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductResponse> page =
                new PageImpl<>(List.of(productResponse));

        when(productService.searchProducts("Laptop", pageable))
                .thenReturn(page);

        ResponseEntity<Page<ProductResponse>> response =
                productController.searchProducts("Laptop", pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(1, response.getBody().getTotalElements());

        assertEquals(
                "Gaming Laptop",
                response.getBody()
                        .getContent()
                        .get(0)
                        .getProductName()
        );

        verify(productService)
                .searchProducts("Laptop", pageable);
    }

    // =========================================================
    // TEST GET ALL PRODUCTS
    // =========================================================

    @Test
    void getAllProducts_ShouldReturnAllProducts() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductResponse> page =
                new PageImpl<>(List.of(productResponse));

        when(productService.getAllProducts(pageable))
                .thenReturn(page);

        ResponseEntity<Page<ProductResponse>> response =
                productController.getAllProducts(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(1, response.getBody().getContent().size());

        verify(productService)
                .getAllProducts(pageable);
    }

    // =========================================================
    // TEST GET PRODUCT BY ID
    // =========================================================

    @Test
    void getProductById_ShouldReturnProduct() {

        Long productId = 15L;

        when(productService.getProductById(productId))
                .thenReturn(productResponse);

        ResponseEntity<ProductResponse> response =
                productController.getProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(
                productId,
                response.getBody().getId()
        );

        assertEquals(
                "Gaming Laptop",
                response.getBody().getProductName()
        );

        verify(productService)
                .getProductById(productId);
    }

    // =========================================================
    // TEST UPDATE PRODUCT
    // =========================================================

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {

        Long productId = 15L;

        when(productService.updateProduct(
                productId,
                productRequest
        )).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response =
                productController.updateProduct(
                        productId,
                        productRequest
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals(
                productId,
                response.getBody().getId()
        );

        verify(productService)
                .updateProduct(productId, productRequest);
    }

    // =========================================================
    // TEST DELETE PRODUCT
    // =========================================================

    @Test
    void deleteProduct_ShouldReturnNoContent() {

        Long productId = 15L;

        doNothing()
                .when(productService)
                .deleteProduct(productId);

        ResponseEntity<Void> response =
                productController.deleteProduct(productId);

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        assertNull(response.getBody());

        verify(productService)
                .deleteProduct(productId);
    }
}