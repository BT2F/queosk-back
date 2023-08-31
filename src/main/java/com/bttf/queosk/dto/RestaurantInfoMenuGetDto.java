package com.bttf.queosk.dto;

import com.bttf.queosk.dto.menudto.MenuDto;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.mapper.MenuDtoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantInfoMenuGetDto {
    RestaurantDto restaurantDto;

    List<MenuDto> menuDtoList;

    public static RestaurantInfoMenuGetDto of(Restaurant restaurant, List<Menu> menuList) {
        List<MenuDto> menuDtoList = new ArrayList<>();
        menuList.forEach(m -> menuDtoList.add(MenuDtoMapper.INSTANCE.MenuToMenuDto(m)));
        return RestaurantInfoMenuGetDto.builder()
                .restaurantDto(RestaurantDto.of(restaurant))
                .menuDtoList(menuDtoList)
                .build();
    }

}
