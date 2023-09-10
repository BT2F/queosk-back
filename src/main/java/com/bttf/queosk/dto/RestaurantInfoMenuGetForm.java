package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
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
    @ApiModel(value = "매장메뉴정보 Response")
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
