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
 * Class BlackListTokenEntity để lưu trữ các token đã bị thu hồi (blacklist) trong Redis,
 * bao gồm token (JWT) và timeToLive (thời gian tồn tại của token trong blacklist, tính bằng giây).
 * Khi token được thêm vào blacklist, nó sẽ tự động bị xóa sau khi timeToLive hết hạn.
 */
@RedisHash("blacklist_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlackListTokenEntity {
    @Id
    String token;

    @TimeToLive(unit = TimeUnit.SECONDS)
    Long timeToLive;
}
