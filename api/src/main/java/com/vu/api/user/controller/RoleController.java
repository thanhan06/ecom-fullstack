package com.vu.api.user.controller;

import com.vu.api.common.ApiResponse;
import com.vu.api.common.ApiResponses;
import com.vu.api.user.DTO.request.AssignPermissionRequest;
import com.vu.api.user.DTO.request.RoleCreateRequest;
import com.vu.api.user.DTO.response.RoleResponse;
import com.vu.api.user.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @Valid @RequestBody RoleCreateRequest request,
            HttpServletRequest req) {
        
        return ApiResponses.created(req, roleService.create(request));
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<Void>> assignPermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody AssignPermissionRequest request,
            HttpServletRequest req) {
        
        roleService.assignPermissions(roleId, request);
        return ApiResponses.ok(req, null);
    }
    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<RoleResponse>>> getAll(HttpServletRequest req) {
        return ApiResponses.ok(req, roleService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest req) {
        roleService.deleteById(id);
        return ApiResponses.ok(req, null);
    }
}
