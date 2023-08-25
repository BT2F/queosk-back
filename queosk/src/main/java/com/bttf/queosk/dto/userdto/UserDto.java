package com.bttf.queosk.dto.userdto;

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
}
