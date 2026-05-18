package com.vu.api.user.DTO.request;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

public record AssignPermissionRequest(@NotEmpty Set<Long> permissionIds) {}
