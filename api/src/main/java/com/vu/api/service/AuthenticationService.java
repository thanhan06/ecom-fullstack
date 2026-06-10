package com.vu.api.service;

import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.AuthenticationRequest;
import com.vu.api.DTO.request.RefreshTokenRequest;
import com.vu.api.DTO.response.AuthenticationResponse;
import com.vu.api.DTO.response.LogoutResponse;
import com.vu.api.DTO.response.RefreshTokenResponse;
import com.vu.api.entity.UserEntity;

@Service
public interface AuthenticationService {
    public String generateToken(UserEntity user);

    public AuthenticationResponse authenticate(AuthenticationRequest request);

    public LogoutResponse logout(String token, String refreshToken);

    public boolean verifyAccessToken(String token);

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshToken);
}
