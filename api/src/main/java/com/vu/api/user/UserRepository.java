package com.vu.api.user;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
    select distinct u from User u
    left join fetch u.userRoles ur
    left join fetch ur.role r
    where u.id = :id
""")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}