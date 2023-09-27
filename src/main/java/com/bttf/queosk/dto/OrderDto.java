package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.MenuItem;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.enumerate.OrderStatus;
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
@ApiModel(value = "Order Dto")
public class OrderDto {
    private Long id;
    private Long tableId;
    private List<MenuItem> menuItems;
    private Long userId;
    private OrderStatus orderStatus;

    public static OrderDto of(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .tableId(order.getTableId())
                .menuItems(order.getMenuItemList())
                .orderStatus(order.getStatus())
                .userId(order.getUserId())
                .build();
    }
}
