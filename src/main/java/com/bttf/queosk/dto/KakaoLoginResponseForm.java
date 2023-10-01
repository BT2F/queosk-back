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
@ApiModel(value = "카카오 로그인 response")
public class KakaoLoginResponseForm {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String nickname;
    private String imageUrl;
    private String loginType;

    public static KakaoLoginResponseForm of(UserSignInDto userSignInDto) {
        return KakaoLoginResponseForm.builder()
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
