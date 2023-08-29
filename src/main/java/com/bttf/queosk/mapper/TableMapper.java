package com.bttf.queosk.mapper;

import com.bttf.queosk.dto.tabledto.TableDto;
import com.bttf.queosk.dto.tabledto.TableForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TableMapper {
    TableMapper INSTANCE = Mappers.getMapper(TableMapper.class);

    TableForm tableDtoToTableForm(TableDto tableDto);
}
