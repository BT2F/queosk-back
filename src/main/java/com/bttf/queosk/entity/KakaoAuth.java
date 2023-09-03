package com.bttf.queosk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoAuth {
    private String kakaoId;
    private String access;
    private String refresh;

    public static KakaoAuth of(String kakaoId, String refreshToken, String accessToken) {

        return KakaoAuth.builder()
                .kakaoId(kakaoId)
                .refresh(refreshToken)
                .access(accessToken)
                .build();
    }
}
