package com.vu.api.controller;

import java.security.Principal;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.DTO.request.UserCreationRequest;
import com.vu.api.DTO.request.UserUpdationRequest;
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

    @PreAuthorize("hasAuthority('ADMIN') or #user_id == authentication.principal.username")
    @PatchMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String user_id,
            @RequestBody @Valid UserUpdationRequest userUpdationRequest,
            HttpServletRequest req) {
        return ApiResponses.ok(req, userService.updateUser(user_id, userUpdationRequest));
    }

    // @GetMapping("/me")
    // public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(HttpServletRequest req) {
    //     return ApiResponses.ok(req, userService.getMyInfo());
    // }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(Principal principal, HttpServletRequest req) {
        String userId = principal.getName(); // Get the authenticated user's ID from the Principal
        return ApiResponses.ok(req, userService.getUserByUserId(userId)); // Fetch and return the user's information
    }
}
