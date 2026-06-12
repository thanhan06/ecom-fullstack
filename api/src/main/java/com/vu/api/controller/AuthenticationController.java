package com.vu.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.DTO.request.AuthenticationRequest;
import com.vu.api.DTO.request.LogoutRequest;
import com.vu.api.DTO.request.RefreshTokenRequest;
import com.vu.api.DTO.response.AuthenticationResponse;
import com.vu.api.DTO.response.LogoutResponse;
import com.vu.api.DTO.response.RefreshTokenResponse;
import com.vu.api.ResponseConfig.ApiResponse;
import com.vu.api.ResponseConfig.ApiResponses;
import com.vu.api.service.AuthenticationService;

import lombok.experimental.FieldDefaults;

/**
 * Class AuthenticationController để xử lý các endpoint liên quan đến xác thực người dùng như đăng nhập, đăng xuất và làm mới token
 */
@RestController
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@RequestMapping("/auth")
public class AuthenticationController {
    // Tiêm AuthenticationService để xử lý logic xác thực
    @Autowired
    AuthenticationService authenticationService;

    /**
     * Endpoint POST /auth/login để xử lý đăng nhập người dùng
     * @param request đối tượng AuthenticationRequest chứa thông tin đăng nhập (username và password)
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @return  ResponseEntity chứa ApiResponse với dữ liệu AuthenticationResponse nếu đăng nhập thành công, hoặc lỗi nếu thất bại
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @RequestBody @Valid AuthenticationRequest request, HttpServletRequest req) {
        return ApiResponses.ok(req, authenticationService.authenticate(request));
    }

    /**
     * Endpoint POST /auth/logout để xử lý đăng xuất người dùng
     * @param logreq đối tượng LogoutRequest chứa thông tin cần thiết để đăng xuất (refresh token)
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu LogoutResponse nếu đăng xuất thành công, hoặc lỗi nếu thất bại
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(
            @RequestBody @Valid LogoutRequest logreq, HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        return ApiResponses.ok(req, authenticationService.logout(token, logreq.refreshToken()));
    }

    /**
     * Endpoint POST /auth/refresh-token để xử lý làm mới token cho người dùng
     * @param request đối tượng RefreshTokenRequest chứa refresh token cần thiết để làm mới token
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu RefreshTokenResponse nếu làm mới token thành công, hoặc lỗi nếu thất bại
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request, HttpServletRequest req) {
        return ApiResponses.ok(req, authenticationService.refreshToken(request));
    }
}
