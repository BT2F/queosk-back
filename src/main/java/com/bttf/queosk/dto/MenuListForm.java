package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MenuListForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "메뉴목록조회 Response")
    public static class Response {
        private List<MenuDto> menuList;

        public static Response of(List<MenuDto> menuList) {
            return Response.builder().menuList(menuList).build();
        }
    }
}
