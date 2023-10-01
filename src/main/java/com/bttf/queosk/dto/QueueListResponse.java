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
public class QueueListResponse {
    private Integer totalQueue;
    private List<QueueDto> queueDtoList;

    public static QueueListResponse of(QueueListDto queueListDto) {
        return QueueListResponse.builder()
                .totalQueue(queueListDto.getTotalQueue())
                .queueDtoList(queueListDto.getQueueDtoList())
                .build();
    }
}

