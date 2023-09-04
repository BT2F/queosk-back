package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class SettlementPriceForm {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long price;

        public static SettlementPriceForm.Response of(Long price) {
            return Response.builder()
                    .price(price)
                    .build();
        }
    }
}
