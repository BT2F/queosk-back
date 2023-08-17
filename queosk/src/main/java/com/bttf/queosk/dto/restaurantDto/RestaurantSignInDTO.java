package com.bttf.queosk.dto.restaurantDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantSignInDTO {
    private String accessToken;
    private String refreshToken;
    private String ownerId;
    private String restaurantName;
    private String imageUrl;
}
