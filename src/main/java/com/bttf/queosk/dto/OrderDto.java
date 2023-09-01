package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.enumerate.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Table table;
    private Menu menu;
    private Integer count;
    private User user;
    private OrderStatus orderStatus;

    public static OrderDto of(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .table(order.getTable())
                .menu(order.getMenu())
                .count(order.getCount())
                .orderStatus(order.getStatus())
                .user(order.getUser())
                .build();
    }
}
