package com.vu.api.user.DTO;

import java.util.List;

public record UserResponse(
        Long id,
        String email,
        List<RoleResponse> roles
) {}