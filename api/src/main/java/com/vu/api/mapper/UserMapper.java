package com.vu.api.mapper;

import org.mapstruct.Mapper;

import com.vu.api.DTO.request.UserCreationRequest;
import com.vu.api.DTO.response.UserResponse;
import com.vu.api.entity.UserEntity;

/**
 * Class UserMapper để chuyển đổi giữa UserEntity và các DTO liên quan đến người dùng,
 * bao gồm UserResponse (để trả về thông tin người dùng cho client) và
 * UserCreationRequest (để nhận dữ liệu tạo mới người dùng từ client).
 * Sử dụng MapStruct để tự động sinh mã chuyển đổi, giúp giảm thiểu lỗi và tăng hiệu suất.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    public UserResponse toUserResponse(UserEntity entity);

    UserEntity toUserEntity(UserCreationRequest request);
}
