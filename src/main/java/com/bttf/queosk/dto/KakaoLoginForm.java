package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class KakaoLoginForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "카카오 로그인 request")
    public static class Request {
        @NotBlank(message = "코드는 비워둘 수 없습니다.")
        private String code;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "카카오 로그인 response")
    public static class Response {
        private Long id;
        private String accessToken;
        private String refreshToken;
        private String email;
        private String nickname;
        private String imageUrl;
        private String loginType;

        public static Response of(UserSignInDto userSignInDto) {
            return Response.builder()
                    .id(userSignInDto.getId())
                    .accessToken(userSignInDto.getAccessToken())
                    .refreshToken(userSignInDto.getRefreshToken())
                    .email(userSignInDto.getEmail())
                    .nickname(userSignInDto.getNickname())
                    .imageUrl(userSignInDto.getImageUrl())
                    .loginType(userSignInDto.getLoginType())
                    .build();
        }
    }
}
