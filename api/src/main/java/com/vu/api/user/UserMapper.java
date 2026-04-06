package com.vu.api.user;

import com.vu.api.user.DTO.RoleResponse;
import com.vu.api.user.DTO.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class UserMapper {

    public UserResponse toResponse(User u) {
        if (u == null) return null;

        List<RoleResponse> roles = (u.getUserRoles() == null ? List.<RoleResponse>of() :
                u.getUserRoles().stream()
                        .map(UserRole::getRole)
                        .filter(r -> r != null)
                        .map(this::toRoleResponse)
                        .sorted(Comparator.comparing(RoleResponse::name))
                        .toList()
        );

        return new UserResponse(u.getId(), u.getEmail(), roles);
    }

    public RoleResponse toRoleResponse(Role r) {
        if (r == null) return null;
        return new RoleResponse(r.getId(), r.getName());
    }
}