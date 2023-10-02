package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "사용자 히스토리 Response")
public class UserHistoryResponseForm {

    List<UserHistoryDto> userHistories;
    public static UserHistoryResponseForm of(List<UserHistoryDto> userHistories) {
        return UserHistoryResponseForm.builder()
                .userHistories(userHistories)
                .build();
    }
}
