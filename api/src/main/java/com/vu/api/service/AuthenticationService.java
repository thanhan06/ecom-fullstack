package com.vu.api.service;

import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.AuthenticationRequest;
import com.vu.api.DTO.response.AuthenticationResponse;
import com.vu.api.entity.UserEntity;

@Service
public interface AuthenticationService {
    public String generateToken(UserEntity user, int expiryInHours);

    public AuthenticationResponse authenticate(AuthenticationRequest request);
}
