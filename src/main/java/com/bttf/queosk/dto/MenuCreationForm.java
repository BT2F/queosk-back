package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCreationForm {
    @NotEmpty(message = "이름은 비워둘 수 없습니다.")
    private String name;
    private String imageUrl;
    private Long price;
}
