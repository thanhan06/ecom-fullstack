package com.vu.api.service;

import org.springframework.stereotype.Service;

/**
 * Interface RefreshTokenService định nghĩa phương thức generateRefreshToken
 * để tạo refresh token mới cho người dùng dựa trên userId.
 * Phương thức này sẽ được triển khai trong lớp RefreshTokenServiceImpl để tạo và
 * lưu refresh token vào cơ sở dữ liệu thông qua RefreshTokenRepository, đồng thời trả về token đã tạo.
 * Phương thức generateRefreshToken sẽ nhận vào userId của người dùng và trả về một chuỗi token mới được tạo ra.
 * Nếu có lỗi trong quá trình tạo refresh token, phương thức có thể ném ra một ngoại lệ.
 */
@Service
public interface RefreshTokenService {
    public String generateRefreshToken(String userId);
}
