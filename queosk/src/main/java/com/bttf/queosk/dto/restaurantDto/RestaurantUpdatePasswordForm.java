package com.bttf.queosk.dto.restaurantDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantUpdatePasswordForm {
    private String oldPassword;
    private String newPassword;
}
