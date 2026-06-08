package com.vu.api.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.UserCreationRequest;
import com.vu.api.DTO.request.UserUpdationRequest;
import com.vu.api.DTO.response.UserResponse;
import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.UserEntity;
import com.vu.api.mapper.UserMapper;
import com.vu.api.repository.UserRepository;
import com.vu.api.service.UserService;

import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public UserResponse getUserByUserId(String user_id) {
        if (userRepository.findByUserId(user_id).isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        Optional<UserEntity> userEntity = userRepository.findByUserId(user_id);
        return userMapper.toUserResponse(userEntity.get());
    }

    @Override
    public UserResponse createUser(UserCreationRequest userCreationRequest) {
        if (userRepository.findByUserId(userCreationRequest.userId()).isPresent()) {
            throw new ApiException(ErrorCode.USER_EXIST);
        }

        UserEntity userEntity = userMapper.toUserEntity(userCreationRequest);
        userEntity.setPassword(
                userCreationRequest.password().isBlank()
                        ? null
                        : passwordEncoder.encode(userCreationRequest.password()));
        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse updateUser(String user_id, UserUpdationRequest userUpdationRequest) {
        Optional<UserEntity> userResult = userRepository.findByUserId(user_id);
        if (userResult.isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        UserEntity userEntity = userResult.get();
        if (userUpdationRequest.username().isPresent()) {
            userEntity.setUsername(userUpdationRequest.username().get());
        }
        if (userUpdationRequest.password().isPresent()) {
            userEntity.setPassword(
                    passwordEncoder.encode(userUpdationRequest.password().get()));
        }
        if (userUpdationRequest.role().isPresent()) {
            userEntity.setRole(userUpdationRequest.role().get());
        }
        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        if (context == null
                || context.getAuthentication() == null
                || !context.getAuthentication().isAuthenticated()) {
            throw new ApiException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        String userId = context.getAuthentication().getName();
        Optional<UserEntity> userEntity = userRepository.findByUserId(userId);
        if (userEntity.isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
        return userMapper.toUserResponse(userEntity.get());
    }
}
