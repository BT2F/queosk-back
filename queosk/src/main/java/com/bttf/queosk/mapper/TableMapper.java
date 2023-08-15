package com.bttf.queosk.mapper;

import com.bttf.queosk.dto.tableDto.TableDto;
import com.bttf.queosk.dto.tableDto.TableForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TableMapper {
    TableMapper INSTANCE = Mappers.getMapper(TableMapper.class);

    TableDto toTableDto(TableForm tableForm);

}
