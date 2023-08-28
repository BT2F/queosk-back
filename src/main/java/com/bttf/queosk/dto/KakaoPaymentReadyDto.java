package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoPaymentReadyDto {
    private String tid;
    private String nextRedirectMobileUrl;
    private String nextRedirectPcUrl;
    private LocalDateTime createdAt;
}
