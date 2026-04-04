package com.vu.api.product.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductUpdateRequest(
        @NotBlank(message = "name must not be blank")
        String name,

        @NotNull(message = "price is required")
        @Positive(message = "price must be positive")
        Double price,

        @NotNull(message = "categoryId is required")
        @Positive(message = "categoryId must be positive")
        Long categoryId
) {}