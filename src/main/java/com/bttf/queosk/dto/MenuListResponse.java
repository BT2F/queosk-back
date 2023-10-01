package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "메뉴목록조회 Response")
public class MenuListResponse {

    private List<MenuDto> menuList;

    public static MenuListResponse of(List<MenuDto> menuList) {
        return MenuListResponse.builder().menuList(menuList).build();
    }
}

