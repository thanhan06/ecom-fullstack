package com.vu.api.user.controller;

import com.nimbusds.jose.JOSEException;
import com.vu.api.common.ApiResponse;
import com.vu.api.common.ApiResponses;
import com.vu.api.user.DTO.request.LogoutRequest;
import com.vu.api.user.DTO.request.RefreshRequest;
import com.vu.api.user.service.AuthenticationService;
import com.vu.api.user.DTO.request.AuthenticationRequest;
import com.vu.api.user.DTO.response.AuthenticationResponse;
import com.vu.api.user.DTO.request.IntrospectRequest;
import com.vu.api.user.DTO.response.IntrospectResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService service;
    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody AuthenticationRequest req,
                                                              HttpServletRequest httpReq) throws JOSEException {
        return ApiResponses.ok(httpReq, service.login(req));

    }

    @PostMapping("/introspect")
    ResponseEntity<ApiResponse<IntrospectResponse>> login(@RequestBody IntrospectRequest req,
                                                          HttpServletRequest httpReq) throws JOSEException, ParseException {
        return ApiResponses.ok(httpReq, service.introspect(req));

    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest req,
                                            HttpServletRequest httpReq) throws ParseException, JOSEException {
        service.logout(req);
        return ApiResponses.ok(httpReq, null);
    }

    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(@RequestBody RefreshRequest req,
                                                              HttpServletRequest httpReq) throws ParseException, JOSEException {
        return ApiResponses.ok(httpReq, service.refreshToken(req));
    }


}
