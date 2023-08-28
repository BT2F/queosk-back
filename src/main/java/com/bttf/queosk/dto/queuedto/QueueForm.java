package com.bttf.queosk.dto.queuedto;

import lombok.Builder;
import lombok.Getter;

public class QueueForm {

    @Getter
    @Builder
    public static class Response {
        private Long id;
        private Long userId;
        private Long restaurantId;
        private Long numberPeople;
    }
}
