package com.bttf.queosk.dto.orderdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderForm {
    private Long tableId;
    private Long restaurantId;
    private Long menuId;
    private Integer count;
}
