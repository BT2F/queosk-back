package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "KakaoPaymentApproval DTO")
public class KakaoPaymentApprovalResponseForm {
    private String aid;
    private String tid;
    private String cid;
    private String partnerOrderId;
    private String partnerUserId;
    private String paymentMethodType;
    private KakaoAmountDto amount;
    private String itemName;
    private String itemCode;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String payload;
}
