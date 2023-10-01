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
@ApiModel(value = "KakaoPaymentCancel Dto")
public class KakaoPaymentCancelResponseForm {
    private String aid;
    private String tid;
    private String cid;
    private String status;
    private String paymentMethodType;
    private String item_name;
    private String item_code;
    private int quantity;
    private int amount;
    private int approvedCancelAmount;
    private int totalCancelAmount;
    private int cancelAvailableAmount;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;
}
