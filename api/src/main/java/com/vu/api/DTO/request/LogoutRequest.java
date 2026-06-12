package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Class LogoutRequest để nhận dữ liệu đăng xuất từ client,
 * bao gồm refreshToken, không được để trống
 */
public record LogoutRequest(@NotBlank(message = "REFRESH_TOKEN_NOT_BLANK") String refreshToken) {}
