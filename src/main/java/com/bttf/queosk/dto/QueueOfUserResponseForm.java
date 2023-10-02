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
public class QueueOfUserResponseForm {

    private List<QueueOfUserDto> userQueues;

    public static QueueOfUserResponseForm of(List<QueueOfUserDto> userQueueList) {
        return QueueOfUserResponseForm.builder().userQueues(userQueueList).build();
    }
}
