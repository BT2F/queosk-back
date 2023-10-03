package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "매장비밀번호 변경 Request")
public class RestaurantPasswordChangeRequestForm {
    private String oldPassword;
    private String newPassword;
}
