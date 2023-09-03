package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


public class UserSignInForm {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "로그인 Request")
    public static class Request {

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 비워둘 수 없습니다.")
        private String email;

        @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이로 입력해주세요.")
        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "로그인 Response")
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
