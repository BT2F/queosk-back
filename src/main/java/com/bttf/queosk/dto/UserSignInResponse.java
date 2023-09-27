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
@ApiModel(value = "사용자 로그인 Response")
public class UserSignInResponse {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String nickname;
    private String imageUrl;
    private String loginType;

    public static UserSignInResponse of(UserSignInDto userSignInDto) {
        return UserSignInResponse.builder()
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
