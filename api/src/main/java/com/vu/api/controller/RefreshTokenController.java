package com.vu.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.service.RefreshTokenService;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/refresh-token")
public class RefreshTokenController {

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/generate/{userId}")
    public ResponseEntity<String> generateRefreshToken(@PathVariable String userId) {
        return ResponseEntity.ok(refreshTokenService.generateRefreshToken(userId));
    }
}
