package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "사용자 회원정보조회 Response")
public class UserDetailsResponseForm {
    private Long id;
    private String nickName;
    private String email;
    private String phone;
    private String status;
    private String imageUrl;
    private String loginType;

    public static UserDetailsResponseForm of(UserDto userDto) {
        return UserDetailsResponseForm.builder()
                .id(userDto.getId())
                .nickName(userDto.getNickName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .status(userDto.getStatus())
                .imageUrl(userDto.getImageUrl())
                .loginType(userDto.getLoginType())
                .build();
    }
}
