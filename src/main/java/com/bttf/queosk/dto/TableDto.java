package com.bttf.queosk.dto;


import com.bttf.queosk.entity.Table;
import com.bttf.queosk.enumerate.TableStatus;
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
