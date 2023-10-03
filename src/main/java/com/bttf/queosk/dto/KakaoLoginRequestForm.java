package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "카카오 로그인 request")
public class KakaoLoginRequestForm {
    @NotBlank(message = "코드는 비워둘 수 없습니다.")
    private String code;
}
