package com.pagacz.flatflex.infrastructure.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToCache(String key, String value) {
        this.addToCache(key, value, 3600);
    }

    public void addToCache(String key, String value, long expirationSeconds) {
        redisTemplate.opsForValue().set(key, value, expirationSeconds, TimeUnit.SECONDS);
    }

    public void cleanCache() {
        RedisConnection redisConnection = this.redisTemplate.getConnectionFactory().getConnection();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) this.redisTemplate.getKeySerializer();
        DefaultStringRedisConnection defaultStringRedisConnection = new DefaultStringRedisConnection(redisConnection, redisSerializer);
        defaultStringRedisConnection.flushAll();
    }

    public void removeFromCache(String key) {
        redisTemplate.delete(key);
    }

    public boolean containsKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
