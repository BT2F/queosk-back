package com.bttf.queosk.dto;

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
public class RestaurantDetailsDto {
    RestaurantDto restaurantDto;
    List<MenuDto> menuDtoList;
    private Long id;

    public static RestaurantDetailsDto of(Restaurant restaurant, List<Menu> menuList) {
        List<MenuDto> menuDtoList = menuList.stream()
                .map(menu -> MenuDto.builder()
                        .restaurantId(menu.getRestaurantId())
                        .name(menu.getName())
                        .imageUrl(menu.getImageUrl())
                        .price(menu.getPrice())
                        .status(menu.getStatus())
                        .build())
                .collect(Collectors.toList());

        return RestaurantDetailsDto.builder()
                .id(restaurant.getId())
                .restaurantDto(RestaurantDto.of(restaurant))
                .menuDtoList(menuDtoList)
                .build();
    }

}
