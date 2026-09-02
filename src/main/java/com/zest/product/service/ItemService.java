package com.zest.product.service;

import com.zest.product.dto.request.ItemRequest;
import com.zest.product.dto.response.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse createItem(Long productId, ItemRequest request);

    List<ItemResponse> getItemsByProductId(Long productId);

    ItemResponse updateItem(
            Long productId,
            Long itemId,
            ItemRequest request
    );

    void deleteItem(
            Long productId,
            Long itemId
    );
}