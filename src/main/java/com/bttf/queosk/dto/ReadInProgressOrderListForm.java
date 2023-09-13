package com.bttf.queosk.dto;

import com.bttf.queosk.enumerate.OrderStatus;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReadInProgressOrderListForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "주문처리중인 주문 Response")
    public static class Response {
        private Long id;
        private Long tableId;
        private MenuDto menu;
        private Integer count;
        private Long userId;
        private OrderStatus orderStatus;

        public static ReadInProgressOrderListForm.Response of(OrderDto orderDto) {
            return ReadInProgressOrderListForm.Response.builder()
                    .id(orderDto.getId())
                    .tableId(orderDto.getTableId())
                    .menu(orderDto.getMenu())
                    .count(orderDto.getCount())
                    .orderStatus(orderDto.getOrderStatus())
                    .userId(orderDto.getUserId())
                    .build();
        }
    }
}
