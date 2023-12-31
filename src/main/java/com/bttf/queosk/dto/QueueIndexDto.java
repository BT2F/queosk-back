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
@ApiModel(value = "Queue Index Dto")
public class QueueIndexDto {
    private Long userQueueIndex;
    private Long queueRemaining;

    public static QueueIndexDto of(Long userQueueIndex) {
        return QueueIndexDto.builder()
                .queueRemaining(userQueueIndex-1==0?0:userQueueIndex-1)
                .userQueueIndex(userQueueIndex)
                .build();
    }
}
