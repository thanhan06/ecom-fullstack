package com.vu.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vu.api.entity.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {}
