package com.bttf.queosk.mapper;

import com.bttf.queosk.dto.menuDto.CreateMenuForm;
import com.bttf.queosk.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy =
        ReportingPolicy.IGNORE)
public interface CreateMenuMapper extends EntityMapper<CreateMenuForm, Menu> {
    CreateMenuMapper MAPPER = Mappers.getMapper(CreateMenuMapper.class);

    Menu toEntity(final CreateMenuForm createMenuForm);
}
