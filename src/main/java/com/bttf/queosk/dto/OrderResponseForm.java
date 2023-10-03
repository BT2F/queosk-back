package com.bttf.queosk.dto;

import com.bttf.queosk.entity.MenuItem;
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
@ApiModel(value = "주문 Response")
public class OrderResponseForm {

    private Long id;
    private Long tableId;
    private List<MenuItem> menuItems;
    private Long userId;
    private OrderStatus orderStatus;

    public static OrderResponseForm of(OrderDto orderDto) {
        return OrderResponseForm.builder()
                .id(orderDto.getId())
                .tableId(orderDto.getTableId())
                .menuItems(orderDto.getMenuItems())
                .orderStatus(orderDto.getOrderStatus())
                .userId(orderDto.getUserId())
                .build();
    }
}
