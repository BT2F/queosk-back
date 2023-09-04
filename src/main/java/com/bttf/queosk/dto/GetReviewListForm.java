package com.bttf.queosk.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GetReviewListForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel(value = "리뷰 리스트 Response")
    public static class Response {
        private Long id;
        private RestaurantDto restaurant;
        private UserDto user;
        private String subject;
        private String content;
        private Double rate;

        public static GetReviewListForm.Response of(ReviewDto reviewDto) {
            return GetReviewListForm.Response.builder()
                    .id(reviewDto.getId())
                    .restaurant(reviewDto.getRestaurant())
                    .user(reviewDto.getUser())
                    .subject(reviewDto.getSubject())
                    .content(reviewDto.getContent())
                    .rate(reviewDto.getRate())
                    .build();
        }
    }
}
