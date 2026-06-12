package com.vu.api.DTO.response;

import java.sql.Timestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Class UserResponse để trả về dữ liệu người dùng,
 * bao gồm psn_cd, userId, username, password, role, status,
 * created_at, updated_at, create_psn_cd và updated_psn_id
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    int psn_cd;
    String userId;
    String username;
    String password;
    String role;
    Boolean status;
    Timestamp created_at;
    Timestamp updated_at;
    int create_psn_cd;
    int updated_psn_id;
}
