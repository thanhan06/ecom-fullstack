package com.vu.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vu.api.entity.RefreshTokenEntity;

/**
 * Interface RefreshTokenRepository để quản lý các refresh token trong Redis,
 * cung cấp các phương thức CRUD để thêm, xóa và truy vấn các refresh token.
 * Sử dụng CrudRepository của Spring Data Redis để tận dụng các phương thức mặc định như save, findById, deleteById, v.v.
 */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {}
