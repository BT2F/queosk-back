package com.bttf.queosk.dto;

import com.bttf.queosk.enumerate.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class TableForm {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long tableId;
        private TableStatus status;

        public static TableForm.Response of(TableDto tableDto) {
            return TableForm.Response.builder()
                    .tableId(tableDto.getTableId())
                    .status(tableDto.getStatus())
                    .build();
        }
    }


}
