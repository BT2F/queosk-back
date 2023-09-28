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
@ApiModel(value = "Token Refresh Dto")
public class TokenRefreshDto {
    private String accessToken;

    public static TokenRefreshDto of(String newAccessToken) {
        return TokenRefreshDto.builder().accessToken(newAccessToken).build();
    }
}
