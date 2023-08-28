package com.bttf.queosk.dto.kakaodto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KaKaoLoginForm {
    @NotBlank(message = "코드는 비워둘 수 없습니다.")
    private String code;
}
