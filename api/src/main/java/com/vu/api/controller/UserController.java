package com.vu.api.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.vu.api.ResponseConfig.ApiResponse;
import com.vu.api.ResponseConfig.ApiResponses;
import com.vu.api.entity.UserEntity;
import com.vu.api.service.UserService;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserEntity>>> getAllUsers(HttpServletRequest req) {
        return ApiResponses.ok(req, userService.getAllUsers());
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<ApiResponse<UserEntity>> getUserByUserId(
            @PathVariable String user_id, HttpServletRequest req) {
        return ApiResponses.ok(req, userService.getUserByUserId(user_id));
    }
}
