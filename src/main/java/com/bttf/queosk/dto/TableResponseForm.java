package com.bttf.queosk.dto;

import com.bttf.queosk.enumerate.TableStatus;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "테이블 Response")
public class TableResponseForm {

    private String tableName;
    private Long tableId;
    private TableStatus status;

    public static TableResponseForm of(TableDto tableDto) {
        return TableResponseForm.builder()
                .tableName(tableDto.getTableName())
                .tableId(tableDto.getTableId())
                .status(tableDto.getStatus())
                .build();
    }


}
