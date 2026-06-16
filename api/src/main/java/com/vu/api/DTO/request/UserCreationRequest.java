package com.vu.api.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Class UserCreationRequest để nhận dữ liệu tạo mới người dùng từ client,
 * bao gồm userId, username, password và role.
 * userId và username không được để trống,
 * username phải có độ dài từ 3 đến 10 ký tự,
 * password phải có độ dài tối thiểu 8 ký tự.
 * Role có thể là "USER" hoặc "ADMIN".
 */
public record UserCreationRequest(
        @NotBlank(message = "USERNAME_BLANK") @Size(min = 3, max = 10, message = "USERNAME_INVALID") String username,
        @Size(min = 8, message = "PASSWORD_TOO_WEAK") String password,
        String role) {}
