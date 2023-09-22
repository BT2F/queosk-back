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
@ApiModel(value = "사용자의 전체 웨이팅 조회 Response")
public class QueueOfUserResponse {

    private List<QueueOfUserDto> userQueues;

    public static QueueOfUserResponse of(List<QueueOfUserDto> userQueueList) {
        return QueueOfUserResponse.builder().userQueues(userQueueList).build();
    }
}
