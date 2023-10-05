package com.bttf.queosk.dto;

import com.bttf.queosk.entity.MenuItem;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.enumerate.OrderStatus;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "사용자 히스토리 Dto")
public class UserHistoryDto {
    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private String restaurantName;
    private List<MenuItem> menuItems;
    private OrderStatus orderStatus;
    private LocalDateTime orderedAt;

    public static UserHistoryDto of(Order order, String restaurantName, List<MenuItem> menuItems){
        return UserHistoryDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .restaurantId(order.getRestaurantId())
                .restaurantName(restaurantName)
                .menuItems(menuItems)
                .orderStatus(order.getStatus())
                .orderedAt(order.getCreatedAt())
                .build();
    }
}
