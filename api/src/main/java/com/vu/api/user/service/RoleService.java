package com.vu.api.user.service;

import com.vu.api.user.DTO.request.AssignPermissionRequest;
import com.vu.api.user.DTO.request.RoleCreateRequest;
import com.vu.api.user.DTO.response.RoleResponse;
import com.vu.api.user.entity.Role;
import com.vu.api.user.mapper.RoleMapper;
import com.vu.api.user.repository.PermissionRepository;
import com.vu.api.user.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository repository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;
    @Transactional
    public RoleResponse create(RoleCreateRequest request) {
        Role role = new Role();
        role.setName(request.name());


        if (request.permissionIds() != null && !request.permissionIds().isEmpty()) {
            var permissions = permissionRepository.findAllById(request.permissionIds());
            role.getPermissions().addAll(permissions);
        }
        
        role = repository.save(role);
        return roleMapper.toResponse(role);
    }

    @Transactional
    public void assignPermissions(Long roleId, AssignPermissionRequest request) {
        Role role = repository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        var permissions = permissionRepository.findAllById(request.permissionIds());

        role.getPermissions().addAll(permissions);
        repository.save(role);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        var roles = repository.findAll();
        return roles.stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
