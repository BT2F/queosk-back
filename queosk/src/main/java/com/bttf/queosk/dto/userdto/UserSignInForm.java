package com.bttf.queosk.dto.userdto;

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
public class UserSignInForm {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 비워둘 수 없습니다.")
    private String email;

    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이로 입력해주세요.")
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;
}
