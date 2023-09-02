package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ImageUrlForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "이미지 경로 Response")
    public static class Response {
        private String imageUrl;

        public static Response of(String url) {
            return Response.builder()
                    .imageUrl(url)
                    .build();
        }
    }
}
