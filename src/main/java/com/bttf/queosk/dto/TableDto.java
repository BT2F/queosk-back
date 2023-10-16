package com.bttf.queosk.dto;


import com.bttf.queosk.entity.Table;
import com.bttf.queosk.enumerate.TableStatus;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@ApiModel(value = "Table Dto")
@NoArgsConstructor
@AllArgsConstructor
public class TableDto {
    private String tableName;
    private Long tableId;
    private TableStatus status;


    public static TableDto of(Table table) {
        return TableDto.builder()
                .tableName(table.getName())
                .tableId(table.getId())
                .status(table.getStatus())
                .build();
    }
}
