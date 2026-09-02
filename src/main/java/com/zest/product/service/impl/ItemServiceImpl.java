package com.zest.product.service.impl;

import com.zest.product.dto.request.ItemRequest;
import com.zest.product.dto.response.ItemResponse;
import com.zest.product.entity.Item;
import com.zest.product.entity.Product;
import com.zest.product.exception.ItemNotFoundException;
import com.zest.product.exception.ItemProductMismatchException;
import com.zest.product.exception.ProductNotFoundException;
import com.zest.product.repository.ItemRepository;
import com.zest.product.repository.ProductRepository;
import com.zest.product.service.ItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    
    private ItemResponse mapToResponse(Item item) {

        return ItemResponse.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .productId(item.getProduct().getId())
                .build();
    }

    // CREATE ITEM
    @Override
    public ItemResponse createItem(
            Long productId,
            ItemRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(productId));

        Item item = Item.builder()
                .itemName(request.getItemName())
                .quantity(request.getQuantity())
                .product(product)
                .build();

        Item savedItem = itemRepository.save(item);

        return mapToResponse(savedItem);
    }

    // GET ALL ITEMS OF PRODUCT
    @Override
    public List<ItemResponse> getItemsByProductId(
            Long productId) {

        // Verify product exists
        productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(productId));

        return itemRepository.findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UPDATE ITEM
    @Override
    public ItemResponse updateItem(
            Long productId,
            Long itemId,
            ItemRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(productId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(itemId));

        // Check item belongs to requested product
        if (!item.getProduct().getId().equals(product.getId())) {
            throw new ItemProductMismatchException(itemId, productId);
        }

        item.setItemName(request.getItemName());
        item.setQuantity(request.getQuantity());

        Item updatedItem = itemRepository.save(item);

        return mapToResponse(updatedItem);
    }

    // DELETE ITEM
    @Override
    public void deleteItem(
            Long productId,
            Long itemId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(productId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ItemNotFoundException(itemId));

        // Check item belongs to requested product
        if (!item.getProduct().getId().equals(product.getId())) {
            throw new ItemProductMismatchException(itemId, productId);
        }

        itemRepository.delete(item);
    }
}