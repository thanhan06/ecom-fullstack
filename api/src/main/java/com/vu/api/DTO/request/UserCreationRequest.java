package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreationRequest(
        @NotBlank(message = "USERID_BLANK") String userId,
        @NotBlank(message = "USERNAME_BLANK") @Size(min = 3, max = 10, message = "USERNAME_INVALID") String username,
        @Size(min = 8, message = "PASSWORD_TOO_WEAK") String password,
        String role) {}
