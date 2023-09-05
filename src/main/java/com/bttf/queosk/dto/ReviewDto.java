package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private RestaurantDto restaurant;
    private UserDto user;
    private String subject;
    private String content;
    private Double rate;

    public static ReviewDto of(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .restaurant(RestaurantDto.of(review.getRestaurant()))
                .user(UserDto.of(review.getUser()))
                .subject(review.getSubject())
                .content(review.getContent())
                .rate(review.getRate())
                .build();
    }
}
