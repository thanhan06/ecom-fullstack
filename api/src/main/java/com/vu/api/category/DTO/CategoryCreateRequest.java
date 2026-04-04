package com.vu.api.category.DTO;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "name is required")
        String name
) {}
