package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "User ID cannot be blank") String userId,
        @NotBlank(message = "Password cannot be blank") String password) {}
