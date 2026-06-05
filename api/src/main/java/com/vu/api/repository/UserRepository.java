package com.vu.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vu.api.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUserId(String user_id);
}
