package com.vu.api.DTO.response;

public record AuthenticationResponse(String token, String refreshToken, Boolean isAuthenticated) {}
