package com.vu.api.user.controller;

import com.vu.api.common.ApiResponse;
import com.vu.api.common.ApiResponses;
import com.vu.api.user.DTO.request.PermissionCreateRequest;
import com.vu.api.user.DTO.response.PermissionResponse;
import com.vu.api.user.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
    PermissionService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAll(HttpServletRequest req) {
        return ApiResponses.ok(req, service.getAll());
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(
            @Valid @RequestBody PermissionCreateRequest body,
            HttpServletRequest req) {
        return ApiResponses.created(req, service.create(body));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest req) {
        service.deleteById(id);
        return ApiResponses.ok(req, null);
    }
}
