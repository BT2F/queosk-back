package com.bttf.queosk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GetRestaurantUserReviewListForm {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private RestaurantDto restaurant;
        private UserDto user;
        private String subject;
        private String content;
        private Double rate;

        public static GetRestaurantUserReviewListForm.Response of(ReviewDto reviewDto) {
            return GetRestaurantUserReviewListForm.Response.builder()
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
