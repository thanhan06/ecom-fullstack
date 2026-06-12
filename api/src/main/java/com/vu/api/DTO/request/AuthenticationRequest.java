package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Class AuthenticationRequest để nhận dữ liệu đăng nhập từ client,
 * bao gồm userId và password, cả hai đều không được để trống
 */
public record AuthenticationRequest(
        @NotBlank(message = "User ID cannot be blank") String userId,
        @NotBlank(message = "Password cannot be blank") String password) {}
