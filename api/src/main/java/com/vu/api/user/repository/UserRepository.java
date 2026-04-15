package com.vu.api.user.repository;

import com.vu.api.user.entity.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
    select distinct u from User u
    left join fetch u.userRoles ur
    left join fetch ur.role r
    where u.id = :id
""")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("""
    select distinct u from User u
    left join fetch u.userRoles ur
    left join fetch ur.role r
""")
    List<User> findAllWithRoles();

    @Query("""
    select distinct u from User u
    left join fetch u.userRoles ur
    left join fetch ur.role r
    where lower(u.email) = lower(:email)
""")
    Optional<User> findByEmailWithRolesIgnoreCase(@Param("email") String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}