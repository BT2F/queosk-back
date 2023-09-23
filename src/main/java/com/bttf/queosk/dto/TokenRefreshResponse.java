package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "토큰재발급 Response")
public class TokenRefreshResponse {
    private String accessToken;

    public static TokenRefreshResponse of(TokenRefreshDto tokenRefreshDto) {
        return TokenRefreshResponse.builder().accessToken(tokenRefreshDto.getAccessToken()).build();
    }
}