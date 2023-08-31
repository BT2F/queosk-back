package com.bttf.queosk.dto;

import com.bttf.queosk.dto.MenuDto;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantInfoMenuGetDto {
    RestaurantDto restaurantDto;

    List<MenuDto> menuDtoList;

    public static RestaurantInfoMenuGetDto of(Restaurant restaurant, List<Menu> menuList) {
        List<MenuDto> menuDtoList = menuList.stream()
                .map(menu -> MenuDto.builder()
                        .restaurantId(menu.getRestaurantId())
                        .name(menu.getName())
                        .imageUrl(menu.getImageUrl())
                        .price(menu.getPrice())
                        .status(menu.getStatus())
                        .build())
                .collect(Collectors.toList());

        return RestaurantInfoMenuGetDto.builder()
                .restaurantDto(RestaurantDto.of(restaurant))
                .menuDtoList(menuDtoList)
                .build();
    }

}
