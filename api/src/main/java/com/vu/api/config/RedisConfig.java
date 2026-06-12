package com.vu.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Class RedisConfig to config Redis
 */
@Configuration
@EnableRedisRepositories
public class RedisConfig {}
