package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {

    private List<OrderdMenu> orderdMenus;

    private Long price;

    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class OrderdMenu {
        private String menu;
        private Integer count;
        private Long price;

        public Long sumOfPrice() {
            return count * price;
        }

        public static OrderdMenu of(SettlementDto.OrderdMenu orderdMenu) {
            return OrderdMenu.builder()
                    .menu(orderdMenu.getMenu())
                    .count(orderdMenu.getCount())
                    .price(orderdMenu.getPrice())
                    .build();
        }
    }

    public static SettlementResponse of(SettlementDto settlementDto) {
        return SettlementResponse.builder()
                .orderdMenus(Collections.singletonList(
                        OrderdMenu.of((SettlementDto.OrderdMenu) settlementDto.getOrderdMenus())
                        ))
                .price(settlementDto.getPrice())
                .build();
    }
}
