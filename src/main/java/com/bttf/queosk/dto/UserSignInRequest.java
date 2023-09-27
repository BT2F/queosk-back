package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "사용자 로그인 Request")
public class UserSignInRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 비워둘 수 없습니다.")
    private String email;

    @Size(min = 4, max = 20, message = "비밀번호는 4~20자 이내로 입력해주세요.")
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
}
