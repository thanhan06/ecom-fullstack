package com.vu.api.user.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PostgreSQL: CITEXT makes comparisons case-insensitive at DB-level
    // (similar idea to specifying a case-insensitive collation in MySQL).
    // NOTE: Requires the DB extension: CREATE EXTENSION IF NOT EXISTS citext;
    @Column(nullable = false, unique = true, length = 100, columnDefinition = "citext")
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(name = "dob")
    private LocalDate dob;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    public void addRole(Role role) {
        UserRole ur = UserRole.builder().user(this).role(role).build();
        userRoles.add(ur);
    }

    public void removeRole(Role role) {
        userRoles.removeIf(ur -> ur.getRole().getId().equals(role.getId()));
    }
}
