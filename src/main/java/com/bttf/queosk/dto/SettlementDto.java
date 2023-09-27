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

    private Long price;

    public static SettlementDto of(List<SettlementDto.OrderdMenu> settlement, Long price) {
        return SettlementDto.builder()
                .orderdMenus(settlement)
                .price(price)
                .build();
    }

    @AllArgsConstructor
    @Getter
    @Builder
    @NoArgsConstructor
    public static class OrderdMenu {
        private String menu;
        private Integer count;
        private Long price;

        public Long sumOfPrice() {
            return count * price;
        }
    }


}
