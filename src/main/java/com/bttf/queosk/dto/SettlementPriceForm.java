package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class SettlementPriceForm {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "정산금액 Response")
    public static class Response {
        private Long price;

        public static SettlementPriceForm.Response of(Long price) {
            return Response.builder()
                    .price(price)
                    .build();
        }
    }
}
