package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RestaurantInfoMenuGetForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        RestaurantDto restaurantDto;
        List<MenuDto> menuDtoList;
        private Long id;

        public static RestaurantInfoMenuGetForm.Response of(RestaurantInfoMenuGetDto restaurantInfoMenuGetDto) {
            return RestaurantInfoMenuGetForm.Response.builder()
                    .id(restaurantInfoMenuGetDto.getId())
                    .restaurantDto(restaurantInfoMenuGetDto.getRestaurantDto())
                    .menuDtoList(restaurantInfoMenuGetDto.getMenuDtoList())
                    .build();
        }
    }
}
