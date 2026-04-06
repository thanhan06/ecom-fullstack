package com.vu.api.user.DTO;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(
        @NotBlank String role
) {}