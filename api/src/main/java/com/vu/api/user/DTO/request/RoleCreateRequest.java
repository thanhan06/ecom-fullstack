package com.vu.api.user.DTO.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateRequest(@NotBlank String name, Set<Long> permissionIds // Optional list of permissions to link
        ) {}
