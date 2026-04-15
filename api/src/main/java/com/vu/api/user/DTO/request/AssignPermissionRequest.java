package com.vu.api.user.DTO.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record AssignPermissionRequest(
        @NotEmpty Set<Long> permissionIds
) {}

