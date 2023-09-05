package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserDetailsForm {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "회원정보조회 Response")
    public static class Response {
        private Long id;
        private String nickName;
        private String email;
        private String phone;
        private String status;
        private String imageUrl;
        private String loginType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response of(UserDto userDto) {
            return Response.builder()
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
}
