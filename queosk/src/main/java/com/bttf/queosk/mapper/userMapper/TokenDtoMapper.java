package com.bttf.queosk.mapper.userMapper;

import com.bttf.queosk.dto.tokenDto.TokenDto;
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
}