package com.vu.api.entity;

import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Class RefreshTokenEntity để lưu trữ các refresh token trong Redis,
 * bao gồm token (refresh token),
 * userId (ID người dùng liên kết với refresh token) và
 * timeToLive (thời gian tồn tại của refresh token, tính bằng ngày).
 * Khi refresh token được tạo ra, nó sẽ tự động bị xóa sau khi timeToLive hết hạn.
 */
@RedisHash("refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenEntity {

    @Id
    String token;

    String userId;

    @TimeToLive(unit = TimeUnit.DAYS)
    Long timeToLive;
}
