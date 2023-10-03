package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@ApiModel(value = "매장 로그인 Request")
public class RestaurantSignInRequestForm {
    @NotBlank(message = "아이디는 비워둘 수 없습니다.")
    private String ownerId;
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
}
