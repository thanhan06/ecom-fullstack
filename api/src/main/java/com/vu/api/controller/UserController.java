package com.vu.api.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.DTO.request.UserCreationRequest;
import com.vu.api.DTO.response.UserResponse;
import com.vu.api.ResponseConfig.ApiResponse;
import com.vu.api.ResponseConfig.ApiResponses;
import com.vu.api.service.UserService;

import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(HttpServletRequest req) {
        return ApiResponses.ok(req, userService.getAllUsers());
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUserId(
            @PathVariable String user_id, HttpServletRequest req) {
        return ApiResponses.ok(req, userService.getUserByUserId(user_id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody @Valid UserCreationRequest userCreationRequest, HttpServletRequest req) {
        return ApiResponses.created(req, userService.createUser(userCreationRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(HttpServletRequest req) {
        return ApiResponses.ok(req, userService.getMyInfo());
    }
}
