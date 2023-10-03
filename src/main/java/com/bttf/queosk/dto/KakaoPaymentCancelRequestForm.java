package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoPaymentCancelRequestForm {
    private String tid;
    private Integer cancelAmount;
    private Integer cancelTaxFreeAmount;
}
