package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantInfoMenuGetForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        RestaurantDto restaurantDto;
        List<MenuDto> menuDtoList;

        public static RestaurantInfoMenuGetForm.Response of(RestaurantInfoMenuGetDto restaurantInfoMenuGetDto) {
            return RestaurantInfoMenuGetForm.Response.builder()
                    .id(restaurantInfoMenuGetDto.getId())
                    .restaurantDto(restaurantInfoMenuGetDto.getRestaurantDto())
                    .menuDtoList(restaurantInfoMenuGetDto.getMenuDtoList())
                    .build();
        }
    }
}
