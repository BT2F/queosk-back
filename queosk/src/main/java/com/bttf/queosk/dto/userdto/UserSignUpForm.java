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
public class UserSignUpForm {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 비워둘 수 없습니다.")
    private String email;

    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이로 입력해주세요.")
    @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
    private String nickName;

    @Size(min = 4, max = 20, message = "비밀번호는 4~20자 이내로 입력해주세요.")
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;

    @Size(min = 10, max = 14, message = "휴대전화번호는 10~14자 이내로 입력해주세요.")
    @NotBlank(message = "휴대전화번호는 비워둘 수 없습니다.")
    private String phone;
}
