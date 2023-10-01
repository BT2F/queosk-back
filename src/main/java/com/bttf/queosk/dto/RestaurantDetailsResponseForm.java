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
@ApiModel(value = "매장상세정보 Response")
public class RestaurantDetailsResponseForm {

    RestaurantDto restaurantDto;
    List<MenuDto> menuDtoList;
    private Long id;

    public static RestaurantDetailsResponseForm of(RestaurantDetailsDto restaurantDetailsDto) {
        return RestaurantDetailsResponseForm.builder()
                .id(restaurantDetailsDto.getId())
                .restaurantDto(restaurantDetailsDto.getRestaurantDto())
                .menuDtoList(restaurantDetailsDto.getMenuDtoList())
                .build();
    }
}

