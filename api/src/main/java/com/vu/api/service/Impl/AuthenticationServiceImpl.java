package com.vu.api.service.Impl;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
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
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vu.api.DTO.request.AuthenticationRequest;
import com.vu.api.DTO.request.RefreshTokenRequest;
import com.vu.api.DTO.response.AuthenticationResponse;
import com.vu.api.DTO.response.LogoutResponse;
import com.vu.api.DTO.response.RefreshTokenResponse;
import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.BlackListTokenEntity;
import com.vu.api.entity.RefreshTokenEntity;
import com.vu.api.entity.UserEntity;
import com.vu.api.repository.BlackListTokenRepository;
import com.vu.api.repository.RefreshTokenRepository;
import com.vu.api.repository.UserRepository;
import com.vu.api.service.AuthenticationService;
import com.vu.api.service.RefreshTokenService;

import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    BlackListTokenRepository blackListTokenRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    @Value("${jwt.valid-duration}")
    Long validDurationInSeconds;

    @Value("${jwt.refreshable-duration}")
    Long refreshableDurationInDays;

    @Override
    public String generateToken(UserEntity user) {
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
                .expirationTime(new Date(Instant.now()
                        .plus(validDurationInSeconds, ChronoUnit.SECONDS)
                        .toEpochMilli()))
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
        String token = generateToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user.getUserId());
        return new AuthenticationResponse(token, refreshToken, isAuthenticated);
    }

    @Override
    public LogoutResponse logout(String token, String refreshToken) {
        // 1. Kiểm tra null/rỗng
        if (token == null || token.isBlank() || refreshToken == null || refreshToken.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        try {
            // 2. Xử lý cắt chuỗi và giải mã Access Token
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            SignedJWT signedJWT = SignedJWT.parse(actualToken);

            // Lấy userId từ Access Token (Thằng đang gọi API)
            String userIdFromAccessToken = signedJWT.getJWTClaimsSet().getSubject();
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            // 3. KIỂM TRA REFRESH TOKEN TRÊN REDIS
            RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
                    .findById(refreshToken)
                    .orElse(null); // Nếu không tìm thấy, có thể nó đã tự hết hạn và biến mất trước đó

            if (refreshTokenEntity != null) {
                // KIỂM TRA QUYỀN SỞ HỮU: Thằng đòi logout có đúng là chủ của Refresh Token này không?
                if (!refreshTokenEntity.getUserId().equals(userIdFromAccessToken)) {
                    // Nếu không khớp, tức là có hành vi gian lận/rút trộm token của người khác
                    throw new ApiException(ErrorCode.USER_NOT_AUTHORIZED);
                }

                // Nếu khớp, danh chính ngôn thuận xóa nó đi
                refreshTokenRepository.delete(refreshTokenEntity);
            }

            // 4. Đưa Access Token vào blacklist (giữ nguyên logic cũ)
            long currentTime = new Date().getTime();
            long remainingTimeInSeconds = (expirationTime.getTime() - currentTime) / 1000;

            if (remainingTimeInSeconds > 0) {
                BlackListTokenEntity blackListToken = new BlackListTokenEntity();
                blackListToken.setToken(actualToken);
                blackListToken.setTimeToLive(remainingTimeInSeconds);
                blackListTokenRepository.save(blackListToken);
            }

            return new LogoutResponse("Logout successful");

        } catch (ParseException e) {
            throw new ApiException(ErrorCode.INVALID_ACCESS_TOKEN);
        } catch (ApiException e) {
            // Ném lại các ApiException của mình (ví dụ lỗi UNAUTHORIZED ở trên)
            throw e;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean verifyAccessToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token.isBlank()) {
            return false; // Token rỗng hoặc chỉ chứa "Bearer "
        }
        if (blackListTokenRepository.existsById(token)) {
            return false; // Token đã bị blacklist
        }
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(Base64.getDecoder().decode(SIGNER_KEY));
            if (!signedJWT.verify(verifier)) {
                return false; // Token không hợp lệ về chữ ký
            }
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date())) {
                return false; // Token đã hết hạn
            }
            return true; // Token còn hạn
        } catch (Exception e) {
            return false; // Token không hợp lệ hoặc lỗi khi phân tích
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        if (refreshTokenRepository.findById(request.refreshToken()).isEmpty()) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        String userId =
                refreshTokenRepository.findById(request.refreshToken()).get().getUserId();
        if (userRepository.findByUserId(userId).isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        UserEntity user = userRepository.findByUserId(userId).get();
        String newAccessToken = generateToken(user);
        boolean isAuthenticated = true; // Nếu đã có refresh token hợp lệ thì chắc chắn user đã authenticate rồi
        return new RefreshTokenResponse(newAccessToken, isAuthenticated);
    }
}
