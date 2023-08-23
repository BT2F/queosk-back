package com.bttf.queosk.dto.restaurantdto;

import com.bttf.queosk.dto.menudto.MenuDto;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.mapper.menumapper.MenuDtoMapper;
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
public class GetRestaurantInfoMenuDto {
    RestaurantDto restaurantDto;

    List<MenuDto> menuDtoList;

    public static GetRestaurantInfoMenuDto of(Restaurant restaurant, List<Menu> menuList) {
        List<MenuDto> menuDtoList = new ArrayList<>();
        menuList.forEach(m -> menuDtoList.add(MenuDtoMapper.INSTANCE.MenuToMenuDto(m)));
        return GetRestaurantInfoMenuDto.builder()
                .restaurantDto(RestaurantDto.of(restaurant))
                .menuDtoList(menuDtoList)
                .build();
    }

}
