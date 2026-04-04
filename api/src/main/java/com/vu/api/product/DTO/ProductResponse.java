package com.vu.api.product.DTO;

public record ProductResponse(
        Long id,
        String name,
        Double price,
        Long categoryId,
        String categoryName
) {}