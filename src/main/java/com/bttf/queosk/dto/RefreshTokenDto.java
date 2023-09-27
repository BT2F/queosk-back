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
@ApiModel(value = "RefreshToken Dto")
public class RefreshTokenDto {
    private String email;
    private String token;

    public static RefreshTokenDto of(String email, String refreshToken) {
        return RefreshTokenDto.builder()
                .email(email)
                .token(refreshToken)
                .build();
    }
}