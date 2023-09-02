package com.bttf.queosk.dto;

import com.bttf.queosk.enumerate.MenuStatus;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MenuStatusForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "메뉴 상태변경 Request")
    public static class Request {
        private MenuStatus status;
    }
}
