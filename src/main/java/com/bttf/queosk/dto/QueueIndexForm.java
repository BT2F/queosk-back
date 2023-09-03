package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class QueueIndexForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "고객 웨이팅번호 조회 Response")
    public static class Response {
        private Long userQueueIndex;

        public static Response of(QueueIndexDto queueIndexDto) {
            return Response.builder()
                    .userQueueIndex(queueIndexDto.getUserQueueIndex() + 1) // 대기번호의 경우 index + 1
                    .build();
        }
    }
}
