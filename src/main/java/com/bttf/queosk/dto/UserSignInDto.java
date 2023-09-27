package com.bttf.queosk.dto;

import com.bttf.queosk.entity.User;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "User SignIn Dto")
public class UserSignInDto {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String nickname;
    private String imageUrl;
    private String loginType;

    public static UserSignInDto of(User user, String userRefreshToken, String userAccessToken) {
        return UserSignInDto.builder()
                .id(user.getId())
                .accessToken(userAccessToken)
                .refreshToken(userRefreshToken)
                .email(user.getEmail())
                .nickname(user.getNickName())
                .imageUrl(user.getImageUrl())
                .loginType(user.getLoginType().toString())
                .build();
    }
}
