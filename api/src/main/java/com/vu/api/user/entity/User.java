package com.vu.api.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(name = "dob")
    private LocalDate dob;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    public void addRole(Role role) {
        UserRole ur = UserRole.builder()
                .user(this)
                .role(role)
                .build();
        userRoles.add(ur);
    }

    public void removeRole(Role role) {
        userRoles.removeIf(ur -> ur.getRole().getId().equals(role.getId()));
    }
}