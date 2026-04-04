package com.vu.api.product.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest(
        @NotBlank String name,
        @NotNull @Min(1) Double price,
        @NotNull @Min(1) Long categoryId
) {}