package com.vu.api.service;

import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
    public String generateRefreshToken(String userId);
}
