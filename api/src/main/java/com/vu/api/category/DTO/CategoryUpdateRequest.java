package com.vu.api.category.DTO;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        @NotBlank(message = "name must not be blank")
        String name
) {}