package com.bttf.queosk.dto;

import com.bttf.queosk.entity.MenuItem;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class OrderCreationForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "주문 생성 Request")
    public static class Request {
        private Long tableId;
        private Long restaurantId;
        private List<MenuItems> menuItems;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MenuItems {
        private Long menu;
        private Integer count;
    }
}
