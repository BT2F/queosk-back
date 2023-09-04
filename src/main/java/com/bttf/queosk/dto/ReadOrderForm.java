package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.enumerate.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReadOrderForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private Table table;
        private Menu menu;
        private Integer count;
        private User user;
        private OrderStatus orderStatus;

        public static ReadOrderForm.Response of(OrderDto orderDto) {
            return ReadOrderForm.Response.builder()
                    .id(orderDto.getId())
                    .table(orderDto.getTable())
                    .menu(orderDto.getMenu())
                    .count(orderDto.getCount())
                    .orderStatus(orderDto.getOrderStatus())
                    .user(orderDto.getUser())
                    .build();
        }
    }
}
