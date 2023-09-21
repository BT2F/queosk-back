package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "웨이팅 등록 Request")
public class QueueCreationRequest {
    @Min(value = 1, message = "식사인원은 1명 이상이어야 합니다.")
    @Max(value = 100, message = "식사인원은 6명 이하이어야 합니다.")
    private Long numberOfParty;
}