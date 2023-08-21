package com.bttf.queosk.dto.menudto;

import com.bttf.queosk.enumerate.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuForm {
    @NotEmpty(message = "이름은 비워둘 수 없습니다.")
    private String name;
    private String imageUrl;
    private Long price;
}
