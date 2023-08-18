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
    @Id // 정책변경으로 인해 아이디 email - > token 으로 교체
    private String token;
    private String email;
}