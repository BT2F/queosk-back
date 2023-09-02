package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderCreationForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        private Long tableId;
        private Long restaurantId;
        private Long menuId;
        private Integer count;
    }
}
