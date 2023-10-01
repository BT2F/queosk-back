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
public class MenuListResponseForm {

    private List<MenuDto> menuList;

    public static MenuListResponseForm of(List<MenuDto> menuList) {
        return MenuListResponseForm.builder().menuList(menuList).build();
    }
}

