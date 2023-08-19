package com.bttf.queosk.dto.tableDto;

import com.bttf.queosk.enumerate.TableStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TableForm {
    private Long tableId;
    private TableStatus status;
}
