package com.vu.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vu.api.entity.BlackListTokenEntity;

@Repository
public interface BlackListTokenRepository extends CrudRepository<BlackListTokenEntity, String> {}
