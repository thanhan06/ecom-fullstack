package com.vu.api.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vu.api.entity.RefreshTokenEntity;
import com.vu.api.repository.RefreshTokenRepository;
import com.vu.api.service.RefreshTokenService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Class RefreshTokenServiceImpl triển khai các phương thức của RefreshTokenService để quản lý refresh token.
 * Nó sử dụng RefreshTokenRepository để lưu trữ và truy xuất refresh token từ cơ sở dữ liệu.
 * Phương thức generateRefreshToken tạo một refresh token mới cho người dùng dựa trên userId
 * và thời gian sống của token được cấu hình trong application properties.
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenServiceImpl implements RefreshTokenService {

    // Inject RefreshTokenRepository để tương tác với cơ sở dữ liệu
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    // Thời gian sống của refresh token được cấu hình trong application properties
    @Value("${jwt.refreshable-duration}")
    Long refreshableDurationInDays;

    /**
     * Phương thức generateRefreshToken tạo một refresh token mới cho người dùng dựa trên userId.
     * Nó tạo một đối tượng RefreshTokenEntity mới, thiết lập userId và thời gian sống của token,
     * sau đó lưu token vào cơ sở dữ liệu thông qua refreshTokenRepository và trả về token đã tạo.
     * @param userId ID của người dùng để tạo refresh token
     * @return String refresh token đã tạo
     * @throws Exception nếu có lỗi trong quá trình tạo refresh token
     */
    @Override
    public String generateRefreshToken(String userId) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUserId(userId);
        refreshToken.setTimeToLive(refreshableDurationInDays);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
    ;
}
