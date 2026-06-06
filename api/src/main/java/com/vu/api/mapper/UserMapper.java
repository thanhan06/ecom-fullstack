package com.vu.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vu.api.DTO.request.UserCreationRequest;
import com.vu.api.DTO.response.UserResponse;
import com.vu.api.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public UserResponse toUserResponse(UserEntity entity);

    @Mapping(target = "created_at", expression = "java(java.sql.Timestamp.from(java.time.Instant.now()))")
    @Mapping(target = "updated_at", expression = "java(java.sql.Timestamp.from(java.time.Instant.now()))")
    @Mapping(target = "status", constant = "true")
    UserEntity toUserEntity(UserCreationRequest request);
}
