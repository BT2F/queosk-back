package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Order;
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
    private Long tableId;
    private MenuDto menu;
    private Integer count;
    private Long userId;
    private OrderStatus orderStatus;

    public static OrderDto of(Order order, Menu menu) {
        return OrderDto.builder()
                .id(order.getId())
                .tableId(order.getTableId())
                .menu(MenuDto.of(menu))
                .count(order.getCount())
                .orderStatus(order.getStatus())
                .userId(order.getUserId())
                .build();
    }
}
