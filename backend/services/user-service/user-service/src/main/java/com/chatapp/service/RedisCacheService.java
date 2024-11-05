package com.chatapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Caching user profile by user ID
    @Cacheable(value = "userProfiles", key = "#userId")
    public Object getUserProfile(String userId) {
        // This method would typically fetch from database
        // For demonstration, returning null
        return null;
    }

    // Update user profile in cache
    @CachePut(value = "userProfiles", key = "#userId")
    public Object updateUserProfile(String userId, Object userProfile) {
        return userProfile;
    }

    // Remove user profile from cache
    @CacheEvict(value = "userProfiles", key = "#userId")
    public void removeUserProfile(String userId) {
    }

    // Generic method to set a value in Redis with expiration
    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // Generic method to get a value from Redis
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Generic method to delete a key from Redis
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    // Check if a key exists
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Set expiration for a key
    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}