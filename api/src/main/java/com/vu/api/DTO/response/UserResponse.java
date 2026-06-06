package com.vu.api.DTO.response;

import java.sql.Timestamp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

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
