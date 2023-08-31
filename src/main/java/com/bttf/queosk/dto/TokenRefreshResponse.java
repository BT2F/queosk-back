package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRefreshResponse {
    private String accessToken;

    public static TokenRefreshResponse of(String newAccessToken) {
        return TokenRefreshResponse.builder().accessToken(newAccessToken).build();
    }
}
