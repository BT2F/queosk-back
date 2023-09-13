package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class QueueOfRestaurantForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "전체 웨이팅 조회 Response")
    public static class Response {
        private Integer totalQueue;

        public static Response of(Integer queue){
            return Response.builder()
                    .totalQueue(queue)
                    .build();
        }
    }
}
