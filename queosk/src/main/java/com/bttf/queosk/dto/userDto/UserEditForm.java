package com.bttf.queosk.dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEditForm {
    @Size(min = 4, max = 12, message = "비밀번호는 4~20자 이내로 입력해주세요.")
    @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
    private String nickName;

    @Size(min = 10, max = 14, message = "휴대전화번호는 10~14자 이내로 입력해주세요.")
    @NotBlank(message = "휴대전화번호는 비워둘 수 없습니다.")
    private String phone;
}
