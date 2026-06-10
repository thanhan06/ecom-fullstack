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

@RestController
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @RequestBody @Valid AuthenticationRequest request, HttpServletRequest req) {
        return ApiResponses.ok(req, authenticationService.authenticate(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(
            @RequestBody @Valid LogoutRequest logreq, HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        return ApiResponses.ok(req, authenticationService.logout(token, logreq.refreshToken()));
    }

    @PostMapping("refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request, HttpServletRequest req) {
        return ApiResponses.ok(req, authenticationService.refreshToken(request));
    }
}
