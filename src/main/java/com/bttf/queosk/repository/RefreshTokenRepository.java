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
    private static final String QUEOSK = "queosk_auth: ";

    public void save(String email, String refreshToken) {
        redisTemplate.opsForValue().set(QUEOSK + email, refreshToken);
        redisTemplate.expire(QUEOSK + email, 14 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public RefreshTokenDto findByEmail(String email) {
        return RefreshTokenDto.of(
                QUEOSK + email, redisTemplate.opsForValue().get(QUEOSK + email)
        );
    }

    public void deleteByEmail(String email) {
        redisTemplate.delete(QUEOSK + email);
    }
}


