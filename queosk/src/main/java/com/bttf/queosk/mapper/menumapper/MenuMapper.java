package com.bttf.queosk.mapper.menumapper;

import com.bttf.queosk.dto.menudto.CreateMenuForm;
import com.bttf.queosk.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MenuMapper {
    MenuMapper INSTANCE = Mappers.getMapper(MenuMapper.class);

    @Mapping(source = "createMenuForm.name", target = "name")
    @Mapping(source = "createMenuForm.imageUrl", target = "imageUrl")
    @Mapping(source = "createMenuForm.price", target = "price")
    @Mapping(source = "restaurantId", target = "restaurantId")
    Menu CreateMenuToMenu(Long restaurantId, CreateMenuForm createMenuForm);
}
