package com.bttf.queosk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "refresh_token", timeToLive = 1209600)
public class KakaoAuth {
    @Id
    private String email;
    private String kakaoId;
    private String access;
    private String refresh;
    public static KakaoAuth of(String email, String kakaoId,
                               String refreshToken, String accessToken) {
        return KakaoAuth.builder()
                .email(email)
                .kakaoId(kakaoId)
                .refresh(refreshToken)
                .access(accessToken)
                .build();
    }
}