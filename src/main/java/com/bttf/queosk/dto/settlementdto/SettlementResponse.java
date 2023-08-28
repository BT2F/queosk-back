package com.bttf.queosk.dto.settlementdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {

    private List<OrderdMenu> orderdMenus;

    private Long price;

    @AllArgsConstructor
    public static class OrderdMenu {
        private String menu;
        private Integer count;
        private Long price;

        public Long sumOfPrice() {
            return count * price;
        }
    }
}
