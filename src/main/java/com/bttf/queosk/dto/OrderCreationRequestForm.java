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
@ApiModel(value = "주문 생성 Request")
public class OrderCreationRequestForm {

    private Long tableId;
    private Long restaurantId;
    private List<MenuItems> menuItems;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MenuItems {
        private Long menu;
        private Integer count;
    }
}
