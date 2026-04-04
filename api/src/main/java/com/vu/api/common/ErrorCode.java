package com.vu.api.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // COMMON
    COMMON_VALIDATION_FAILED("COMMON_VALIDATION_FAILED", HttpStatus.BAD_REQUEST, "Validation failed"),
    COMMON_BAD_REQUEST("COMMON_BAD_REQUEST", HttpStatus.BAD_REQUEST, "Bad request"),
    COMMON_INTERNAL_ERROR("COMMON_INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"),

    // CATEGORY
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", HttpStatus.NOT_FOUND, "Category not found"),
    CATEGORY_NAME_EXISTS("CATEGORY_NAME_EXISTS", HttpStatus.CONFLICT, "Category name already exists"),

    // PRODUCT
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", HttpStatus.NOT_FOUND, "Product not found"),
    PRODUCT_NAME_EXISTS("PRODUCT_NAME_EXISTS", HttpStatus.CONFLICT, "Product name already exists");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String code() { return code; }
    public HttpStatus status() { return status; }
    public String message() { return message; }
}