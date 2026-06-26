package com.vu.api.DTO.request;

import org.openapitools.jackson.nullable.JsonNullable;

public record ProductUpdationRequest(
        JsonNullable<String> productName,
        JsonNullable<Long> price,
        JsonNullable<Integer> productAmount,
        JsonNullable<String> productTypeId) {}
