package com.vu.api.user.repository;

import com.vu.api.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
        boolean existsByName(String name);
}
