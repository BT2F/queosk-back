package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NotNull
@Builder
public class UserImageUrlDto {
    private String imagePath;

    public static UserImageUrlDto of(String url) {
        return UserImageUrlDto.builder().imagePath(url).build();
    }
}
