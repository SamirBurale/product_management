package com.zest.product.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    // =========================================================
    // PRODUCT NOT FOUND
    // =========================================================

    @Test
    void handleProductNotFound_shouldReturn404() {

        ProductNotFoundException exception =
                new ProductNotFoundException(15L);

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleProductNotFound(
                        exception,
                        null
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "PRODUCT_NOT_FOUND",
                response.getBody().getError()
        );

        assertEquals(
                exception.getMessage(),
                response.getBody().getMessage()
        );

        assertEquals(
                404,
                response.getBody().getStatus()
        );

        assertNotNull(response.getBody().getTimestamp());
    }

    // =========================================================
    // ITEM NOT FOUND
    // =========================================================

    @Test
    void handleItemNotFound_shouldReturn404() {

        ItemNotFoundException exception =
                mock(ItemNotFoundException.class);

        when(exception.getMessage())
                .thenReturn("Item not found");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleItemNotFound(exception);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "ITEM_NOT_FOUND",
                response.getBody().getError()
        );

        assertEquals(
                "Item not found",
                response.getBody().getMessage()
        );

        assertEquals(
                404,
                response.getBody().getStatus()
        );

        assertNotNull(response.getBody().getTimestamp());
    }

    // =========================================================
    // ITEM PRODUCT MISMATCH
    // =========================================================

    @Test
    void handleItemProductMismatch_shouldReturn400() {

        ItemProductMismatchException exception =
                mock(ItemProductMismatchException.class);

        when(exception.getMessage())
                .thenReturn("Item does not belong to product");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleItemProductMismatch(
                        exception
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "ITEM_PRODUCT_MISMATCH",
                response.getBody().getError()
        );

        assertEquals(
                "Item does not belong to product",
                response.getBody().getMessage()
        );

        assertEquals(
                400,
                response.getBody().getStatus()
        );

        assertNotNull(response.getBody().getTimestamp());
    }

    // =========================================================
    // VALIDATION ERROR
    // =========================================================

    @Test
    void handleValidationException_shouldReturn400() {

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult =
                mock(BindingResult.class);

        FieldError fieldError =
                new FieldError(
                        "productRequest",
                        "productName",
                        "Product name is required"
                );

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleValidationException(
                        exception,
                        null
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "VALIDATION_ERROR",
                response.getBody().getError()
        );

        assertEquals(
                "productName: Product name is required",
                response.getBody().getMessage()
        );

        assertEquals(
                400,
                response.getBody().getStatus()
        );

        assertNotNull(response.getBody().getTimestamp());
    }

 // =========================================================
 // GENERAL EXCEPTION
 // =========================================================

 @Test
 void handleGeneralException_shouldReturn500() {

     Exception exception =
             new RuntimeException("Database error");

     ResponseEntity<ErrorResponse> response =
             exceptionHandler.handleException(exception);

     assertEquals(
             HttpStatus.INTERNAL_SERVER_ERROR,
             response.getStatusCode()
     );

     assertNotNull(response.getBody());

     assertEquals(
             "INTERNAL_SERVER_ERROR",
             response.getBody().getError()
     );

     assertEquals(
             "An unexpected error occurred",
             response.getBody().getMessage()
     );

     assertEquals(
             500,
             response.getBody().getStatus()
     );

     assertNotNull(response.getBody().getTimestamp());
 }
}