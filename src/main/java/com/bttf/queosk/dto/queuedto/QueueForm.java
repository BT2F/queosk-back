package com.bttf.queosk.dto.queuedto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;


public class QueueForm {

    @Builder
    @Getter
    public static class Request {
        @NotBlank(message = "인원 수는 비워둘 수 없습니다.")
        private Long numberPeople;
        private Long ab;
    }

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long userId;
        private Long restaurantId;
        private Long numberPeople;

    }


}
