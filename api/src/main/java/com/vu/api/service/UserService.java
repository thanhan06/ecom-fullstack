package com.vu.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vu.api.entity.UserEntity;

@Service
public interface UserService {
    public List<UserEntity> getAllUsers();

    public UserEntity getUserByUserId(String user_id);
}
