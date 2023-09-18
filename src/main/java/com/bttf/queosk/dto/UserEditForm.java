package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class UserEditForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "회원정보변경 Request")
    public static class Request {
        @Size(min = 2, max = 10, message = "닉네임은 2~10자 이내로 입력해주세요.")
        private String nickName;

        @Size(min = 10, max = 14, message = "휴대전화번호는 10~14자 이내로 입력해주세요.")
        private String phone;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "회원정보변경 Response")
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
