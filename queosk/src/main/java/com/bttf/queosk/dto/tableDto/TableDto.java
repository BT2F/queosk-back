package com.bttf.queosk.dto.tableDto;

import com.bttf.queosk.dto.enumerate.TableStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TableDto {
    private Long tableId;
    private TableStatus status;

}
