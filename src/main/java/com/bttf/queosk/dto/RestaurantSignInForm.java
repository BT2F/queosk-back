package com.bttf.queosk.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class RestaurantSignInForm {
    @Getter
    @Builder
    public static class Request {
        @NotBlank(message = "아이디는 비워둘 수 없습니다.")
        private String ownerId;
        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        private String password;
    }

    @Getter
    @Builder
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
                    .ownerId(restaurantSignInDto.getOwnerId())
                    .restaurantName(restaurantSignInDto.getRestaurantName())
                    .imageUrl(restaurantSignInDto.getImageUrl())
                    .build();
        }
    }
}
