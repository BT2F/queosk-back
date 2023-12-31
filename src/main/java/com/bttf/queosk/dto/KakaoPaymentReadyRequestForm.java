package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoPaymentReadyRequestForm {
    @NotEmpty(message = "상품 이름은 필수 항목입니다.")
    private String itemName;
    @NotEmpty(message = "상품 코드는 필수 항목입니다.")
    private String itemCode;
    private Integer quantity;
    private Integer totalAmount;
    private Integer taxFreeAmount;
    private Integer vatAmount;
    private Integer greenDeposit;
    private Integer installMonth;

    private Long tableId;
    private Long restaurantId;
}
