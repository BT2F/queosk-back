package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "사용자 회원정보변경 Request")
public class UserEditRequest {
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 이내로 입력해주세요.")
    @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
    private String nickName;

    @Size(min = 10, max = 14, message = "휴대전화번호는 10~14자 이내로 입력해주세요.")
    @NotBlank(message = "휴대전화번호는 비워둘 수 없습니다.")
    private String phone;
}
