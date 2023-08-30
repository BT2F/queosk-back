package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReviewForm {
    @NotNull
    private Long restaurantId;
    @NotBlank
    private String subject;
    private String content;
    @DecimalMin(value= "0.0")
    @DecimalMax(value= "5.0")
    private Double rate;
}
