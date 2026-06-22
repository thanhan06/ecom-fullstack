package com.vu.api.DTO.response;

public record ProductResponse(
        String productId,
        String productName,
        boolean status,
        String description,
        String productImg,
        Integer productAmount,
        Long price,
        String productTypeId) {}
