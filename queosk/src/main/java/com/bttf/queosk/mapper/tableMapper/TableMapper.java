package com.bttf.queosk.mapper.tableMapper;

import com.bttf.queosk.dto.enumerate.TableStatus;
import com.bttf.queosk.dto.tableDto.TableDto;
import com.bttf.queosk.dto.tableDto.TableForm;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Table;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TableMapper {
    TableMapper INSTANCE = Mappers.getMapper(TableMapper.class);

    TableForm tableDtoToTableForm(TableDto tableDto);

    TableDto tableToTableDto(Table table);

    Table restaurantToTable(Restaurant restaurant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "restaurant", source = "restaurantId")
    Table updateStatusWithDto(TableStatus status, TableDto tableDto);
}
