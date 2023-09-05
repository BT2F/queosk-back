package com.bttf.queosk.repository;

import com.bttf.queosk.entity.KakaoAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class KakaoAuthRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KAKAO = "kakao_auth: ";

    public void save(String email, KakaoAuth kakaoAuth) {
        redisTemplate.opsForValue().set(KAKAO + email, kakaoAuth);
        redisTemplate.expire(KAKAO + email, 14 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public KakaoAuth findById(String email) {
        return (KakaoAuth) redisTemplate.opsForValue().get(KAKAO + email);
    }

    public void deleteByEmail(String email) {
        redisTemplate.delete(KAKAO + email);
    }
}
