package com.vu.api.service.Impl;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vu.api.DTO.request.AuthenticationRequest;
import com.vu.api.DTO.response.AuthenticationResponse;
import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.UserEntity;
import com.vu.api.repository.UserRepository;
import com.vu.api.service.AuthenticationService;

import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    @Override
    public String generateToken(UserEntity user, int expiryInHours) {
        // Validate signer key
        if (SIGNER_KEY == null || SIGNER_KEY.isBlank()) {
            throw new IllegalStateException("JWT signer key is not configured (jwt.signerKey)");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(SIGNER_KEY);
        } catch (IllegalArgumentException ex) {
            // If not valid Base64, fall back to using raw UTF-8 bytes (but still validate length)
            keyBytes = SIGNER_KEY.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 64) { // HS512 requires 512-bit key (64 bytes)
            throw new IllegalArgumentException("JWT signer key is too short for HS512; need at least 64 bytes");
        }

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512)
                .type(JOSEObjectType.JWT)
                .build();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserId())
                .issuer("ecom.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(expiryInHours, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", List.of(user.getRole()))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        try {
            signedJWT.sign(new MACSigner(keyBytes));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (userRepository.findByUserId(request.userId()).isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        UserEntity user = userRepository.findByUserId(request.userId()).get();
        Boolean isAuthenticated = passwordEncoder.matches(request.password(), user.getPassword());
        if (!isAuthenticated) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }
        String token = generateToken(user, 2);
        String refreshToken = generateToken(user, 24);
        return new AuthenticationResponse(token, refreshToken, isAuthenticated);
    }
}
