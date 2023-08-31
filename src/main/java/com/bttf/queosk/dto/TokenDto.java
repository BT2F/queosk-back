package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.enumerate.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDto {
    private String email;
    private Long id;
    private UserRole userRole;

    public static TokenDto of(Restaurant restaurant) {
        return TokenDto.builder()
                .email(restaurant.getEmail())
                .id(restaurant.getId())
                .userRole(restaurant.getUserRole())
                .build();
    }

    public static TokenDto of(User user) {
        return TokenDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .userRole(user.getUserRole())
                .build();
    }

    public static TokenDto of(Long id, UserRole userRole, String email) {
        return TokenDto.builder()
                .id(id)
                .userRole(userRole)
                .email(email)
                .build();
    }
}
