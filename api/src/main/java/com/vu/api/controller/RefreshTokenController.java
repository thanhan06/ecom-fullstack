package com.vu.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.service.RefreshTokenService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Class RefreshTokenController để xử lý các endpoint liên quan đến refresh token
 */
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/refresh-token")
public class RefreshTokenController {

    // Tiêm RefreshTokenService để xử lý logic liên quan đến refresh token
    @Autowired
    RefreshTokenService refreshTokenService;

    /**
     * Endpoint POST /refresh-token/generate/{userId} để tạo mới refresh token cho người dùng
     * @param userId là ID của người dùng cần tạo refresh token
     * @return ResponseEntity chứa refresh token mới được tạo ra nếu thành công, hoặc lỗi nếu thất bại
     */
    @PostMapping("/generate/{userId}")
    public ResponseEntity<String> generateRefreshToken(@PathVariable String userId) {
        return ResponseEntity.ok(refreshTokenService.generateRefreshToken(userId));
    }
}
