package com.bttf.queosk.dto.menuDto;

import com.bttf.queosk.dto.enumerate.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuForm {
    @NotEmpty(message = "이름은 비워둘 수 없습니다.")
    private String name;
    private String imageUrl;
    @NotEmpty(message = "가격은 비워둘 수 없습니다.")
    private Long price;
    private MenuStatus status;
}
