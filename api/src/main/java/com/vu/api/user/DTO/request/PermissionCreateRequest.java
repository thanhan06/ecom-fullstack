package com.vu.api.user.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionCreateRequest(
        @NotBlank String name,
        @NotBlank @Size(min = 6, max = 200) String description
) {}