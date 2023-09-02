package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

public class MenuCreationForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "메뉴 등록 Response")
    public static class Request {
        @NotEmpty(message = "이름은 비워둘 수 없습니다.")
        private String name;
        private String imageUrl;
        private Long price;
    }
}
