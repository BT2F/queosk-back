package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class RestaurantSignInForm {
    @Getter
    @Builder
    @ApiModel(value = "매장 로그인 Request")
    public static class Request {
        @NotBlank(message = "아이디는 비워둘 수 없습니다.")
        private String ownerId;
        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        private String password;
    }

    @Getter
    @Builder
    @ApiModel(value = "매장 로그인 Response")
    public static class Response {
        private Long id;
        private String accessToken;
        private String refreshToken;
        private String ownerId;
        private String restaurantName;
        private String imageUrl;

        public static RestaurantSignInForm.Response of(RestaurantSignInDto restaurantSignInDto) {
            return Response.builder()
                    .id(restaurantSignInDto.getId())
                    .accessToken(restaurantSignInDto.getAccessToken())
                    .refreshToken(restaurantSignInDto.getRefreshToken())
                    .ownerId(restaurantSignInDto.getOwnerId())
                    .restaurantName(restaurantSignInDto.getRestaurantName())
                    .imageUrl(restaurantSignInDto.getImageUrl())
                    .build();
        }
    }
}
