package com.vu.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.UserCreationRequest;
import com.vu.api.DTO.response.UserResponse;

@Service
public interface UserService {
    public List<UserResponse> getAllUsers();

    public UserResponse getUserByUserId(String user_id);

    public UserResponse createUser(UserCreationRequest userCreationRequest);

    public UserResponse getMyInfo();
}
