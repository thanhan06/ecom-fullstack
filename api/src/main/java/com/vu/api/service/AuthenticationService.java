package com.vu.api.service;

import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.AuthenticationRequest;
import com.vu.api.DTO.request.RefreshTokenRequest;
import com.vu.api.DTO.response.AuthenticationResponse;
import com.vu.api.DTO.response.LogoutResponse;
import com.vu.api.DTO.response.RefreshTokenResponse;
import com.vu.api.entity.UserEntity;

/**
 * Interface AuthenticationService định nghĩa các phương thức liên quan đến xác thực người dùng, bao gồm:
 * - generateToken: Tạo token JWT cho người dùng dựa trên thông tin của UserEntity.
 * - authenticate: Xác thực người dùng dựa trên yêu cầu AuthenticationRequest và trả về AuthenticationResponse chứa token và thông tin người dùng.
 * - logout: Đăng xuất người dùng bằng cách hủy token và refresh token, trả về LogoutResponse chứa thông tin về kết quả đăng xuất.
 * - verifyAccessToken: Kiểm tra tính hợp lệ của access token.
 * - refreshToken: Tạo một access token mới dựa trên refresh token đã cung cấp trong RefreshTokenRequest, trả về RefreshTokenResponse chứa access token mới.
 */
@Service
public interface AuthenticationService {
    public String generateToken(UserEntity user);

    public AuthenticationResponse authenticate(AuthenticationRequest request);

    public LogoutResponse logout(String token, String refreshToken);

    public boolean verifyAccessToken(String token);

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshToken);
}
