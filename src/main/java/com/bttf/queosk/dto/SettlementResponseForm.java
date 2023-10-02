package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "정산 Response")
public class SettlementResponseForm {
    private List<OrderdMenu> orderedMenus;

    private Long total;

    public static SettlementResponseForm of(SettlementDto settlementDto) {
        List<OrderdMenu> orderedMenus = settlementDto.getOrderdMenus().stream()
                .map(OrderdMenu::of)
                .collect(Collectors.toList());

        return SettlementResponseForm.builder()
                .orderedMenus(orderedMenus)
                .total(settlementDto.getTotal())
                .build();
    }

    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @Getter
    public static class OrderdMenu {
        private String menu;
        private Integer count;
        private Long price;

        public static OrderdMenu of(SettlementDto.OrderdMenu orderdMenu) {
            return OrderdMenu.builder()
                    .menu(orderdMenu.getMenu())
                    .count(orderdMenu.getCount())
                    .price(orderdMenu.getTotal())
                    .build();
        }

    }
}


