package com.vu.api.user.DTO.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record RoleCreateRequest(
        @NotBlank String name,
        Set<Long> permissionIds // Optional list of permissions to link
) {}

