package com.vu.api.DTO.request;

import jakarta.validation.constraints.Size;

import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Class UserUpdationRequest để nhận dữ liệu cập nhật người dùng từ client,
 * bao gồm username, password và role. username phải có độ dài từ 3 đến 10 ký tự nếu được cung cấp,
 * password phải có độ dài tối thiểu 8 ký tự nếu được cung cấp.
 * Role có thể là "USER" hoặc "ADMIN" nếu được cung cấp.
 * Tất cả các trường đều là JsonNullable để cho phép cập nhật một phần thông tin người dùng
 * mà không cần phải cung cấp tất cả các trường.
 */
public record UserUpdationRequest(
        @Size(min = 3, max = 10, message = "USERNAME_INVALID") JsonNullable<String> username,
        @Size(min = 8, message = "PASSWORD_TOO_WEAK") JsonNullable<String> password,
        JsonNullable<String> role) {}
