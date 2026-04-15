package com.vu.api.user.controller;

import com.vu.api.common.ApiResponse;
import com.vu.api.common.ApiResponses;
import com.vu.api.user.DTO.request.AssignRoleRequest;
import com.vu.api.user.DTO.request.UserCreateRequest;
import com.vu.api.user.DTO.response.UserResponse;
import com.vu.api.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserCreateRequest req,
                                                            HttpServletRequest httpReq) {
        System.out.println("======> REQUEST RECEIVED <======");
        System.out.println("Email: " + req.email());
        System.out.println("DOB in Request: " + req.dob());
        return ApiResponses.created(httpReq, service.create(req));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfor(HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.getMyInfo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id,
                                                             HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.getById(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.getAllUsers());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id,
                                                            @Valid @RequestBody com.vu.api.user.DTO.request.UserUpdateRequest req,
                                                            HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.update(id, req));
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> assignRole(@PathVariable Long id,
                                                                @Valid @RequestBody AssignRoleRequest req,
                                                                HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.assignRole(id, req.role()));
    }

    @DeleteMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> removeRole(@PathVariable Long id,
                                                                @Valid @RequestBody AssignRoleRequest req,
                                                                HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.removeRole(id, req.role()));
    }
}