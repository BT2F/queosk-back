package com.bttf.queosk.dto.orderdto;

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
    private Table table;
    private Menu menu;
    private Long count;
    private User user;
    private OrderStatus orderStatus;

    public static OrderDto of(Order order) {
        return OrderDto.builder()
                .table(order.getTable())
                .menu(order.getMenu())
                .count(order.getCount())
                .orderStatus(order.getStatus())
                .user(order.getUser())
                .build();
    }
}
