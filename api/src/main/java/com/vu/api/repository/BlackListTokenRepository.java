package com.vu.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vu.api.entity.BlackListTokenEntity;

/**
 * Interface BlackListTokenRepository để quản lý các token đã bị thu hồi (blacklist) trong Redis,
 * cung cấp các phương thức CRUD để thêm, xóa và truy vấn các token trong blacklist.
 * Sử dụng CrudRepository của Spring Data Redis để tận dụng các phương thức mặc định như save, findById, deleteById, v.v.
 */
@Repository
public interface BlackListTokenRepository extends CrudRepository<BlackListTokenEntity, String> {}
