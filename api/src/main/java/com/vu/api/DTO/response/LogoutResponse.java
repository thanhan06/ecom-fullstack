package com.vu.api.DTO.response;

/**
 * Class LogoutResponse để trả về dữ liệu sau khi người dùng đăng xuất thành công,
 * bao gồm message để thông báo kết quả của việc đăng xuất
 */
public record LogoutResponse(String message) {}
