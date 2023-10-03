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
@ApiModel(value = "고객 웨이팅번호 조회 Response")
public class QueueIndexResponseForm {

    private Long userQueueIndex;
    public static QueueIndexResponseForm of(QueueIndexDto queueIndexDto) {
        return QueueIndexResponseForm.builder()
                .userQueueIndex(queueIndexDto.getUserQueueIndex() + 1) // 대기번호의 경우 index + 1
                .build();
    }
}
