package com.bttf.queosk.dto;

import com.bttf.queosk.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String nickName;
    private String email;
    private String phone;
    private String status;
    private String imageUrl;
    private String loginType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserDto of(User user) {
        return UserDto.builder()
                .nickName(user.getNickName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus().toString())
                .imageUrl(user.getImageUrl())
                .loginType(user.getLoginType().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
