package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(@NotBlank(message = "REFRESH_TOKEN_NOT_BLANK") String refreshToken) {}
