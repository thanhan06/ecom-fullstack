package com.vu.api.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vu.api.user.DTO.request.RoleCreateRequest;
import com.vu.api.user.DTO.response.RoleResponse;
import com.vu.api.user.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    Role toEntity(RoleCreateRequest request);

    RoleResponse toResponse(Role role);
}
