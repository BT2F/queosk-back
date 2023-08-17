package com.bttf.queosk.mapper.userMapper;

import com.bttf.queosk.dto.userDto.UserSignInDto;
import com.bttf.queosk.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserSignInMapper {
    UserSignInMapper INSTANCE = Mappers.getMapper(UserSignInMapper.class);

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.nickName", target = "nickname")
    @Mapping(source = "user.imageUrl", target = "imageUrl")
    UserSignInDto userToUserSignInDto(User user, String refreshToken, String accessToken);
}
