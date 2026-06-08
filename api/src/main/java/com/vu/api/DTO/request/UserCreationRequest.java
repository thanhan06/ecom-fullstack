package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreationRequest(
        @NotBlank(message = "User ID cannot be blank") String userId,
        @NotBlank(message = "Username cannot be blank") String username,
        @Size(min = 8, message = "PASSWORD_TOO_WEAK") String password,
        String role) {}
