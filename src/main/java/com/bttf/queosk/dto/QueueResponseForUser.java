package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueResponseForUser {
    private Long userQueueIndex;
    private Long queueRemaining;

    public static QueueResponseForUser of(Long userQueueIndex){
        return QueueResponseForUser.builder()
                .queueRemaining(userQueueIndex)
                .userQueueIndex(userQueueIndex + 1) // 대기번호의 경우 index + 1
                .build();
    }
}
