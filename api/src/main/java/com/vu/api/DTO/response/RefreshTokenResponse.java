package com.vu.api.DTO.response;

/**
 * Class RefreshTokenResponse để trả về dữ liệu sau khi người dùng làm mới token thành công,
 * bao gồm token (JWT) mới và isAuthenticated (để xác định xem người dùng đã được xác thực hay chưa)
 */
public record RefreshTokenResponse(String token, boolean isAuthenticated) {}
