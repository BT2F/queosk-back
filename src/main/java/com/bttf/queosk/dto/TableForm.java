package com.bttf.queosk.dto;

import com.bttf.queosk.enumerate.TableStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TableForm {
    private Long tableId;
    private TableStatus status;

    public static TableForm of(TableDto tableDto) {
        return TableForm.builder()
                .tableId(tableDto.getTableId())
                .status(tableDto.getStatus())
                .build();
    }
}
