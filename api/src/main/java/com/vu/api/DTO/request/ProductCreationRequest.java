package com.vu.api.DTO.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreationRequest(
        @NotBlank(message = "PRODUCT_NAME_NOT_BLANK") String productName,
        String description,
        String productImg,
        @NotNull(message = "PRODUCT_AMOUNT_NOT_NULL") @Min(value = 0, message = "PRODUCT_AMOUNT_NOT_NEGATIVE")
                Integer productAmount,
        @NotNull(message = "PRODUCT_PRICE_NOT_NULL") @Min(value = 0, message = "PRODUCT_PRICE_NOT_NEGATIVE") Long price,
        @NotBlank(message = "PRODUCT_TYPE_ID_NOT_BLANK") String productTypeId) {}
