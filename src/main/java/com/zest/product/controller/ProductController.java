package com.zest.product.controller;

import com.zest.product.dto.request.ProductRequest;
import com.zest.product.dto.response.ProductResponse;
import com.zest.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    // =========================================================
    // CREATE PRODUCT
    // Only ADMIN can create a product
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // SEARCH PRODUCTS
    // USER + ADMIN can search
    // =========================================================

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam("name") String name,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                productService.searchProducts(name, pageable)
        );
    }


    // =========================================================
    // GET ALL PRODUCTS
    // USER + ADMIN can view
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                productService.getAllProducts(pageable)
        );
    }


    // =========================================================
    // GET PRODUCT BY ID
    // USER + ADMIN can view
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable("id") Long id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }


    // =========================================================
    // UPDATE PRODUCT
    // Only ADMIN can update
    // =========================================================

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                productService.updateProduct(id, request)
        );
    }


    // =========================================================
    // DELETE PRODUCT
    // Only ADMIN can delete
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("id") Long id) {

        productService.deleteProduct(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}