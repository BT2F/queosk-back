package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderCreationForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "주문 생성 Request")
    public static class Request {
        private Long tableId;
        private Long restaurantId;
        private Long menuId;
        private Integer count;
    }
}
