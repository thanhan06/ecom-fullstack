package com.vu.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vu.api.entity.UserEntity;

/**
 * Interface UserRepository để quản lý các thực thể người dùng trong cơ sở dữ liệu,
 * cung cấp các phương thức CRUD để thêm, xóa và truy vấn các người dùng.
 * Sử dụng JpaRepository của Spring Data JPA để tận dụng các phương thức mặc định như save, findById, deleteById, v.v.
 * Ngoài ra, định nghĩa phương thức findByUserId để tìm kiếm người dùng theo userId.
 */
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUserId(String userId);

    @Query(value = "SELECT MAX(u.user_id) FROM mstuser u WHERE u.role = :role", nativeQuery = true)
    String findMaxUserId(@Param("role") String role);
}
