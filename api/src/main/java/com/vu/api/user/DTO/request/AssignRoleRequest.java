package com.vu.api.user.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(
        @NotBlank String role
) {}