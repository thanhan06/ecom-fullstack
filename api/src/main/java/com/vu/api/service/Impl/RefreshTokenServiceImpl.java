package com.vu.api.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vu.api.entity.RefreshTokenEntity;
import com.vu.api.repository.RefreshTokenRepository;
import com.vu.api.service.RefreshTokenService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refreshable-duration}")
    Long refreshableDurationInDays;

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
