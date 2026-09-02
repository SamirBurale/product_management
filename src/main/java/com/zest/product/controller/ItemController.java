package com.zest.product.controller;

import com.zest.product.dto.request.ItemRequest;
import com.zest.product.dto.response.ItemResponse;
import com.zest.product.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // =========================================================
    // CREATE ITEM
    // ADMIN ONLY
    // =========================================================

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ItemResponse> createItem(

            @PathVariable("productId") Long productId,

            @Valid @RequestBody ItemRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(itemService.createItem(productId, request));
    }

    // =========================================================
    // GET ALL ITEMS
    // USER + ADMIN
    // =========================================================

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<ItemResponse>> getItemsByProductId(

            @PathVariable("productId") Long productId) {

        return ResponseEntity.ok(
                itemService.getItemsByProductId(productId)
        );
    }

    // =========================================================
    // UPDATE ITEM
    // ADMIN ONLY
    // =========================================================

    @PutMapping("/{itemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ItemResponse> updateItem(

            @PathVariable("productId") Long productId,

            @PathVariable("itemId") Long itemId,

            @Valid @RequestBody ItemRequest request) {

        return ResponseEntity.ok(
                itemService.updateItem(
                        productId,
                        itemId,
                        request
                )
        );
    }

    // =========================================================
    // DELETE ITEM
    // ADMIN ONLY
    // =========================================================

    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteItem(

            @PathVariable("productId") Long productId,

            @PathVariable("itemId") Long itemId) {

        itemService.deleteItem(productId, itemId);

        return ResponseEntity.noContent().build();
    }
}