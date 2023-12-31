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
@ApiModel(value = "주문처리중인 주문 Response")
public class OrderListResponseForm {

    private Long id;
    private Long tableId;
    private List<MenuItem> menuItems;
    private Long userId;
    private OrderStatus orderStatus;

    public static OrderListResponseForm of(OrderDto orderDto) {
        return OrderListResponseForm.builder()
                .id(orderDto.getId())
                .tableId(orderDto.getTableId())
                .menuItems(orderDto.getMenuItems())
                .orderStatus(orderDto.getOrderStatus())
                .userId(orderDto.getUserId())
                .build();
    }
}
