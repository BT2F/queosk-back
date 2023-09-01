package com.bttf.queosk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "refresh_token")
public class RefreshToken {
    @Id
    private String token;
    private String email;

    public static RefreshToken of(User user, String refreshToken) {
        return RefreshToken.builder()
                .email(user.getEmail())
                .token(refreshToken)
                .build();
    }
}