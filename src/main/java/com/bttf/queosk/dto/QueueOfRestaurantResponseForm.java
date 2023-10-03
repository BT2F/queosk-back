package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "전체 웨이팅 조회 Response")
public class QueueOfRestaurantResponseForm {

    private Integer totalQueue;

    public static QueueOfRestaurantResponseForm of(Integer queue) {
        return QueueOfRestaurantResponseForm.builder()
                .totalQueue(queue)
                .build();
    }
}
