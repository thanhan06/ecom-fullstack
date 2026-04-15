package com.vu.api.user.DTO.response;

public record PermissionResponse(
        Long id,
        String name,
        String description
) {}