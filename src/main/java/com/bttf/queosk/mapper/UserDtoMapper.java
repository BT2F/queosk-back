package com.bttf.queosk.mapper;

import com.bttf.queosk.dto.userdto.UserDto;
import com.bttf.queosk.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserDtoMapper {
    UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.nickName", target = "nickName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.status", target = "status")
    @Mapping(source = "user.imageUrl", target = "imageUrl")
    @Mapping(source = "user.loginType", target = "loginType")
    @Mapping(source = "user.createdAt", target = "createdAt")
    @Mapping(source = "user.updatedAt", target = "updatedAt")
    UserDto userToUserDto(User user);
}
