package com.vu.api.user.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vu.api.common.ApiException;
import com.vu.api.common.ErrorCode;
import com.vu.api.config.SecurityBeans;
import com.vu.api.user.DTO.request.AuthenticationRequest;
import com.vu.api.user.DTO.request.LogoutRequest;
import com.vu.api.user.DTO.request.RefreshRequest;
import com.vu.api.user.DTO.response.AuthenticationResponse;
import com.vu.api.user.DTO.request.IntrospectRequest;
import com.vu.api.user.DTO.response.IntrospectResponse;
import com.vu.api.user.entity.InvalidatedToken;
import com.vu.api.user.entity.User;
import com.vu.api.user.repository.InvalidatedTokenRepository;
import com.vu.api.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    SecurityBeans securityBeans;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNING_KEY;
    public boolean authenticate(AuthenticationRequest request){
        var user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return securityBeans.passwordEncoder().matches(request.password(), user.getPasswordHash());


    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.token();
        boolean isValid = true;
        try{
            verifyToken(token);
        }catch (ApiException e){
            isValid = false;
        }
        return new IntrospectResponse(isValid);

    }
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) throws JOSEException {
        if (authenticate(request)){
            User user = userRepository.findByEmailWithRolesIgnoreCase(request.email())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
            var token = generateToken(user);
            return new AuthenticationResponse(true,token);
        } else {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }
    }

    private String generateToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("api.vu.com")
                .issueTime(new java.util.Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("scope", buildScope(user)) // TODO: lấy roles thực tế của user
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(
                header,
                payload
        );
        try{
                jwsObject.sign(new MACSigner(SIGNING_KEY.getBytes()));
                return jwsObject.serialize();
            } catch (JOSEException e){
                log.error("Cannot create token",e);
                throw new RuntimeException("Failed to sign JWT", e);
        }

    }

    private String buildScope(User user){
        StringJoiner sb = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getUserRoles())){
            user.getUserRoles().forEach(ur -> {
                sb.add("ROLE_" + ur.getRole().getName());
                if (!CollectionUtils.isEmpty(ur.getRole().getPermissions())) {
                    ur.getRole().getPermissions().forEach(p -> sb.add(p.getName()));
                }
            });
        }
        return sb.toString();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signedJwt = verifyToken(request.token());
        String jti = signedJwt.getJWTClaimsSet().getJWTID();
        Date exp = signedJwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(exp)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

    }

    private SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNING_KEY.getBytes());
        Date exp = SignedJWT.parse(token).getJWTClaimsSet().getExpirationTime();
        SignedJWT signedJWT = SignedJWT.parse(token);


        if(!(signedJWT.verify(verifier) && exp.after(new Date())))
            throw new ApiException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new ApiException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;


    }

    @Transactional
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJwt = verifyToken(request.token());
        
        String jti = signedJwt.getJWTClaimsSet().getJWTID();
        Date exp = signedJwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(exp)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);
        
        String email = signedJwt.getJWTClaimsSet().getSubject();
        
        User user = userRepository.findByEmailWithRolesIgnoreCase(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        var token = generateToken(user);
        return new AuthenticationResponse(true, token);
    }
}
