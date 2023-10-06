package com.bttf.queosk.dto;

import com.bttf.queosk.entity.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuItemDto {
    private Long menuPrice;
    private Integer count;

    public static MenuItemDto of(MenuItem menuItem){
        return MenuItemDto.builder()
                .menuPrice(menuItem.getMenu().getPrice())
                .count(menuItem.getCount())
                .build();
    }

    public MenuItemDto(Long menuPrice){
        this.menuPrice = menuPrice;
        this.count = 0;
    }
    public MenuItemDto addCount(int countToAdd) {
        this.count += countToAdd;
        return this;
    }
}
