package com.bttf.queosk.repository;

import com.bttf.queosk.dto.RefreshTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String email, String refreshToken) {
        redisTemplate.opsForValue().set(email, refreshToken);
        redisTemplate.expire(email, 14 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public RefreshTokenDto findById(String email) {
        return RefreshTokenDto.of(email, redisTemplate.opsForValue().get(String.valueOf(email)));
    }

    public void deleteById(String email){
        redisTemplate.delete(email);
    }
}


