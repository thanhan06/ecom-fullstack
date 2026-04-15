package com.vu.api.user.mapper;

import com.vu.api.user.DTO.request.PermissionCreateRequest;
import com.vu.api.user.DTO.response.PermissionResponse;
import com.vu.api.user.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "id", ignore = true)
    Permission toEntity(PermissionCreateRequest request);

    PermissionResponse toResponse(Permission permission);
}
