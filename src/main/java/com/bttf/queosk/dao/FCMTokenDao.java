package com.bttf.queosk.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FCMTokenDao {

    private final StringRedisTemplate tokenRedisTemplate;

    public void saveToken(String email, String token) {
        tokenRedisTemplate.opsForValue()
                .set(email, token);
    }

    public String getToken(String email) {
        return tokenRedisTemplate.opsForValue().get(email);
    }

    public void deleteToken(String email) {
        tokenRedisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return tokenRedisTemplate.hasKey(email);
    }
}