package com.vu.api.DTO.response;
/**
 * Class AuthenticationResponse để trả về dữ liệu sau khi người dùng đăng nhập thành công,
 * bao gồm token (JWT), refreshToken và isAuthenticated (để xác định xem người dùng đã được xác thực hay chưa)
 */
public record AuthenticationResponse(String token, String refreshToken, Boolean isAuthenticated) {}
