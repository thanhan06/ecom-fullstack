package com.vu.api.user.service;

import com.vu.api.user.DTO.request.PermissionCreateRequest;
import com.vu.api.user.DTO.response.PermissionResponse;
import com.vu.api.user.mapper.PermissionMapper;
import com.vu.api.user.repository.PermissionRepository;
import com.vu.api.user.entity.Permission;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionCreateRequest req) {
        Permission p = permissionMapper.toEntity(req);
        p = permissionRepository.save(p);
        return permissionMapper.toResponse(p);
    }

    public List<PermissionResponse> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .map(permissionMapper::toResponse)
                .toList();
    }
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }
}
