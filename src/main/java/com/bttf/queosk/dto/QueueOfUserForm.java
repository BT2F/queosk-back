package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class QueueOfUserForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "사용자의 전체 웨이팅 조회 Response")

    public static class Response {
        private List<QueueOfUserDto> userQueues;

        public static Response of(List<QueueOfUserDto> userQueueList) {
            return Response.builder().userQueues(userQueueList).build();
        }
    }
}