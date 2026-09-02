package com.zest.product.service;

import com.zest.product.dto.request.ProductRequest;
import com.zest.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
    
    Page<ProductResponse> searchProducts(
            String name,
            Pageable pageable);
}