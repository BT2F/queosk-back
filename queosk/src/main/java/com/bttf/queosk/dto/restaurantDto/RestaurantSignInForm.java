package com.bttf.queosk.dto.restaurantDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSignInForm {
    @NotBlank(message = "아이디는 비워둘 수 없습니다.")
    private String ownerId;
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
}
