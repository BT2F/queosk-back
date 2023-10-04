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
@ApiModel(value = "전체 웨이팅 조회 Response")
public class QueueListResponseForm {
    private Integer totalQueue;
    private List<QueueDto> queueDtoList;

    public static QueueListResponseForm of(QueueListDto queueListDto) {
        return QueueListResponseForm.builder()
                .totalQueue(queueListDto.getTotalQueue())
                .queueDtoList(queueListDto.getQueueDtoList())
                .build();
    }
}

