package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Settlement Dto")
public class SettlementDto {
    private List<OrderdMenu> orderdMenus;

    private Long total;

    public static SettlementDto of(List<SettlementDto.OrderdMenu> settlement, Long price) {
        return SettlementDto.builder()
                .orderdMenus(settlement)
                .total(price)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderdMenu {
        private String menu;
        private Long menuPrice;
        private Integer count;
    }
}
