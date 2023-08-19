package com.bttf.queosk.dto.tableDto;

import com.bttf.queosk.dto.enumerate.TableStatus;
import com.bttf.queosk.entity.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TableDto {
    private Long tableId;
    private TableStatus status;


    public static TableDto of(Table table) {
        return TableDto.builder()
                .tableId(table.getId())
                .status(table.getStatus())
                .build();
    }
}
