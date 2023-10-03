package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(value = "매장 로그인 Response")
public class RestaurantSignInResponseForm {

    private Long id;
    private String accessToken;
    private String refreshToken;
    private String ownerId;
    private String restaurantName;
    private String imageUrl;

    public static RestaurantSignInResponseForm of(RestaurantSignInDto restaurantSignInDto) {
        return RestaurantSignInResponseForm.builder()
                .id(restaurantSignInDto.getId())
                .accessToken(restaurantSignInDto.getAccessToken())
                .refreshToken(restaurantSignInDto.getRefreshToken())
                .ownerId(restaurantSignInDto.getOwnerId())
                .restaurantName(restaurantSignInDto.getRestaurantName())
                .imageUrl(restaurantSignInDto.getImageUrl())
                .build();
    }
}

