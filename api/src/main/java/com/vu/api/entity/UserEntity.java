package com.vu.api.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Class UserEntity để lưu trữ thông tin người dùng trong cơ sở dữ liệu,
 * bao gồm psn_cd (ID người dùng), userId, username, password, role, status,
 * created_at, updated_at, create_psn_cd và updated_psn_id
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table(name = "mstuser")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int psn_cd;

    @Column(name = "user_id", unique = true, nullable = false)
    String userId;

    String username;
    String password;
    String role;

    @Builder.Default
    Boolean status = true;

    @CreationTimestamp
    @Column(updatable = false)
    Timestamp created_at;

    @UpdateTimestamp
    Timestamp updated_at;

    int create_psn_cd;
    int update_psn_cd;
}
