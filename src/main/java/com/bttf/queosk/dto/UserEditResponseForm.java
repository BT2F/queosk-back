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
@ApiModel(value = "사용자 회원정보변경 Response")
public class UserEditResponseForm {
    private Long id;
    private String nickName;
    private String email;
    private String phone;
    private String status;
    private String imageUrl;
    private String loginType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserEditResponseForm of(UserDto userDto) {
        return UserEditResponseForm.builder()
                .id(userDto.getId())
                .nickName(userDto.getNickName())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .status(userDto.getStatus())
                .imageUrl(userDto.getImageUrl())
                .loginType(userDto.getLoginType())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(userDto.getUpdatedAt())
                .build();
    }
}
