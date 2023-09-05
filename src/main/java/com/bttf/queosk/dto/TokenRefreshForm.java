package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TokenRefreshForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "토큰재발급 Request")
    public static class Request {
        private String refresh_token;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "토큰재발급 Response")
    public static class Response {
        private String accessToken;

        public static Response of(TokenRefreshDto tokenRefreshDto) {
            return Response.builder().accessToken(tokenRefreshDto.getAccessToken()).build();
        }
    }
}