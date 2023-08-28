package com.bttf.queosk.mapper.tablemapper;

import com.bttf.queosk.dto.tabledto.TableDto;
import com.bttf.queosk.dto.tabledto.TableForm;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Table;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TableMapper {
    TableMapper INSTANCE = Mappers.getMapper(TableMapper.class);

    TableForm tableDtoToTableForm(TableDto tableDto);

    Table restaurantToTable(Restaurant restaurant);
}
