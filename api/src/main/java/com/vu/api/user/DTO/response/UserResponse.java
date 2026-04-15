package com.vu.api.user.DTO.response;

import java.time.LocalDate;
import java.util.List;

public record UserResponse(
        Long id,
        String email,
        LocalDate dob,
        List<RoleResponse> roles
) {}