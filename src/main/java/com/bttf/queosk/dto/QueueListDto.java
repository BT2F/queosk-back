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
@ApiModel(value = "Queue List Dto")
public class QueueListDto {
    private Integer totalQueue;
    private List<QueueDto> queueDtoList;

    public static QueueListDto of(List<QueueDto> queueDtos) {
        return QueueListDto.builder()
                .totalQueue(queueDtos.size())
                .queueDtoList(queueDtos)
                .build();
    }
}
