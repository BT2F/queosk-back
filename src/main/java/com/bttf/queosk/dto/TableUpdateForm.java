package com.bttf.queosk.dto;

import com.bttf.queosk.enumerate.TableStatus;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(value = "테이블 업데이트 Request")
public class TableUpdateForm {
    TableStatus tableStatus;
    String name;
}
