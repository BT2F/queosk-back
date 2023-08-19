package com.bttf.queosk.mapper.usermapper;

import com.bttf.queosk.dto.tokendto.TokenDto;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TokenDtoMapper {
    TokenDtoMapper INSTANCE = Mappers.getMapper(TokenDtoMapper.class);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.userRole", target = "userRole")
    TokenDto userToTokenDto(User user);


    @Mapping(source = "restaurant.id", target = "id")
    @Mapping(source = "restaurant.email", target = "email")
    @Mapping(source = "restaurant.userRole", target = "userRole")
    TokenDto restaurantToTokenDto(Restaurant restaurant);
}
