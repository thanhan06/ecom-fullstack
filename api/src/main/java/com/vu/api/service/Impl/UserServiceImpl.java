package com.vu.api.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.UserEntity;
import com.vu.api.repository.UserRepository;
import com.vu.api.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity getUserByUserId(String user_id) {
        return userRepository.findByUserId(user_id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
