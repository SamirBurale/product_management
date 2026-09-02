package com.zest.product.exception;

public class ItemProductMismatchException extends RuntimeException {

    public ItemProductMismatchException(Long itemId, Long productId) {
        super("Item " + itemId +
                " does not belong to product " + productId);
    }
}