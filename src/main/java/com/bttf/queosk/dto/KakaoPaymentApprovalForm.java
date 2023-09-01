package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoPaymentApprovalForm {
    private String cid;
    private String tid;
    private String partnerOrderId;
    private String payload;
    private Integer totalAmount;
}
