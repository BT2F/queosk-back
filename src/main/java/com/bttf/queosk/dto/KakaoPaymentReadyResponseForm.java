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
@ApiModel(value = "KakaoPaymentReady Dto")
public class KakaoPaymentReadyResponseForm {
    private String tid;
    private String nextRedirectMobileUrl;
    private String nextRedirectPcUrl;
    private String OrderId;
    private LocalDateTime createdAt;
}
