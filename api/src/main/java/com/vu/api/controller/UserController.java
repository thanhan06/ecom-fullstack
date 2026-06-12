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

/**
 * Class UserController để xử lý các endpoint liên quan đến người dùng như lấy thông tin người dùng, tạo mới người dùng và cập nhật thông tin người dùng
 */
@RestController
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@RequestMapping("/users")
public class UserController {
    // Tiêm UserService để xử lý logic liên quan đến người dùng
    @Autowired
    UserService userService;

    /**
     * Endpoint GET /users để lấy danh sách tất cả người dùng (chỉ dành cho Admin)
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là danh sách UserResponse nếu thành công, hoặc lỗi nếu thất bại
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(HttpServletRequest req) {
        return ApiResponses.ok(req, userService.getAllUsers());
    }

    /**
     * Endpoint GET /users/{user_id} để lấy thông tin chi tiết của một người dùng theo user_id (dành cho Admin hoặc chính người dùng đó)
     * @param user_id là ID của người dùng cần lấy thông tin
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là UserResponse nếu thành công, hoặc lỗi nếu thất bại
     */
    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUserId(
            @PathVariable String user_id, HttpServletRequest req) {
        return ApiResponses.ok(req, userService.getUserByUserId(user_id));
    }

    /**
     * Endpoint POST /users để tạo mới một người dùng (dành cho tất cả mọi người, không cần token)
     * @param userCreationRequest đối tượng UserCreationRequest chứa thông tin cần thiết để tạo
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là UserResponse của người dùng mới
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody @Valid UserCreationRequest userCreationRequest, HttpServletRequest req) {
        return ApiResponses.created(req, userService.createUser(userCreationRequest));
    }

    /**
     * Endpoint PATCH /users/{user_id} để cập nhật thông tin của một người dùng theo user_id (dành cho Admin hoặc chính người dùng đó)
     * @param user_id là ID của người dùng cần cập nhật thông tin
     * @param userUpdationRequest đối tượng UserUpdationRequest chứa thông tin cần thiết để cập nhật người dùng
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là UserResponse của người dùng sau khi được cập nhật nếu thành công, hoặc lỗi nếu thất bại
     */
    @PreAuthorize("hasAuthority('ADMIN') or #user_id == authentication.principal.username")
    @PatchMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String user_id,
            @RequestBody @Valid UserUpdationRequest userUpdationRequest,
            HttpServletRequest req) {
        return ApiResponses.ok(req, userService.updateUser(user_id, userUpdationRequest));
    }

    /**
     * Endpoint GET /users/me để lấy thông tin của người dùng hiện đang đăng nhập (dành cho tất cả người dùng đã xác thực)
     * @param principal đối tượng Principal chứa thông tin của người dùng đã xác thực
     * @param req đối tượng HttpServletRequest để lấy thông tin của request (dùng cho logging)
     * @return ResponseEntity chứa ApiResponse với dữ liệu là UserResponse của người dùng hiện tại nếu thành công, hoặc lỗi nếu thất bại
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(Principal principal, HttpServletRequest req) {
        String userId = principal.getName(); // Get the authenticated user's ID from the Principal
        return ApiResponses.ok(req, userService.getUserByUserId(userId)); // Fetch and return the user's information
    }
}
