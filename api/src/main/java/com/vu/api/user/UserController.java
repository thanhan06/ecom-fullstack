package com.vu.api.user;

import com.vu.api.common.ApiResponse;
import com.vu.api.common.ApiResponses;
import com.vu.api.user.DTO.AssignRoleRequest;
import com.vu.api.user.DTO.UserCreateRequest;
import com.vu.api.user.DTO.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ApiResponses.created(httpReq, service.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id,
                                                             HttpServletRequest httpReq) {
        return ApiResponses.ok(httpReq, service.getById(id));
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