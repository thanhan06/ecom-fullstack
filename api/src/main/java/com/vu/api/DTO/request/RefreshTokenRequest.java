package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Class RefreshTokenRequest để nhận dữ liệu làm mới token từ client,
 * bao gồm refreshToken, không được để trống
 */
public record RefreshTokenRequest(@NotBlank String refreshToken) {}
