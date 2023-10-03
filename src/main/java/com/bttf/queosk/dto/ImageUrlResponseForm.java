package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "이미지 경로 Response")
public class ImageUrlResponseForm {

    private String imageUrl;
    public static ImageUrlResponseForm of(String url) {
        return ImageUrlResponseForm.builder()
                .imageUrl(url)
                .build();
    }
}
