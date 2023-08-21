package com.bttf.queosk.mapper.menumapper;

import com.bttf.queosk.dto.menudto.MenuCreationForm;
import com.bttf.queosk.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MenuMapper {
    MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);

    @Mapping(source = "menuCreationForm.name", target = "name")
    @Mapping(source = "menuCreationForm.imageUrl", target = "imageUrl")
    @Mapping(source = "menuCreationForm.price", target = "price")
    @Mapping(source = "restaurantId", target = "restaurantId")
    Menu MenuCreationFormToMenu(Long restaurantId, MenuCreationForm menuCreationForm);
}
