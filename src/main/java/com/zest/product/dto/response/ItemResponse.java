package com.zest.product.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemResponse {

    private Long id;
    private String itemName;
    private Integer quantity;
    private Long productId;
}