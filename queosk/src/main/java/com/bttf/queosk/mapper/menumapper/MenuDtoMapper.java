package com.bttf.queosk.mapper.menumapper;

import com.bttf.queosk.dto.menudto.MenuDto;
import com.bttf.queosk.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MenuDtoMapper {
    MenuDtoMapper INSTANCE = Mappers.getMapper(MenuDtoMapper.class);

    @Mapping(source = "menu.name", target = "name")
    @Mapping(source = "menu.imageUrl", target = "imageUrl")
    @Mapping(source = "menu.price", target = "price")
    @Mapping(source = "menu.restaurantId", target = "restaurantId")
    @Mapping(source = "menu.id", target = "id")
    MenuDto CreateMenuToMenu(Menu menu);
}
