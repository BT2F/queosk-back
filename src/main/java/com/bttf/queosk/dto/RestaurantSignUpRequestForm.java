package com.bttf.queosk.dto;

import com.bttf.queosk.enumerate.RestaurantCategory;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "매장 회원가입 Request")
public class RestaurantSignUpRequestForm {

    @NotBlank(message = "아이디는 비워둘 수 없습니다.")
    private String ownerId;

    @NotBlank(message = "이름은 비워둘 수 없습니다.")
    private String ownerName;

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    private String password;

    @Email(message = "이메일 형식에 맞춰주세요.")
    private String email;

    @Pattern(regexp = "^\\d{3}(?:\\d{3}|\\d{4})\\d{4}$", message =
            "올바른 전화번호가 아닙니다. 다음의 형식으로 입력해 주세요. \"00000000000\"")
    private String phone;

    @NotBlank(message = "가게명은 비워둘 수 없습니다.")
    private String restaurantName;

    @Pattern(regexp = "^\\d{3}(?:\\d{3}|\\d{4})\\d{4}$", message =
            "올바른 전화번호가 아닙니다. 다음의 형식으로 입력해 주세요. \"00000000000\"")
    private String restaurantPhone;

    @NotNull(message = "업종명은 비워둘 수 없습니다.")
    private RestaurantCategory category;

    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$",
            message = "올바른 사업자 번호가 아닙니다. 다음의 형식으로 입력 해 주세요 \"000-00-00000\"")
    private String businessNumber;

    private String businessStartDate;

    @NotBlank(message = "주소는 비워둘 수 없습니다.")
    private String address;
}
