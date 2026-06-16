package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record ProductTypeRequest(@NotBlank(message = "PRODUCT_TYPE_NAME_IS_NOT_BLANK") String productTypeName) {}
