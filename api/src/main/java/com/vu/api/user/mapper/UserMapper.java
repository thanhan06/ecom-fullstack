package com.vu.api.user.mapper;

import com.vu.api.user.DTO.response.RoleResponse;
import com.vu.api.user.DTO.response.UserResponse;
import com.vu.api.user.entity.User;
import com.vu.api.user.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Objects;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected RoleMapper roleMapper;

    @Mapping(target = "roles", expression = "java(mapRoles(u.getUserRoles()))")
    public abstract UserResponse toResponse(User u);

    public List<RoleResponse> mapRoles(Set<UserRole> userRoles) {
        if (userRoles == null) {
            return Collections.emptyList();
        }
        return userRoles.stream()
                .map(UserRole::getRole)
                .filter(Objects::nonNull)
                .map(roleMapper::toResponse)
                .sorted(Comparator.comparing(RoleResponse::name))
                .toList();
    }
}