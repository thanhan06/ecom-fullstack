package com.vu.api.user.DTO.response;


import java.util.Set;

public record RoleResponse(Long id, String name, Set<PermissionResponse> permissions ) {}