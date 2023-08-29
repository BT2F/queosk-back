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
    private Restaurant restaurant;
    private User user;
    private String subject;
    private String content;
    private Double rate;

    public static ReviewDto of(Review review) {
        return ReviewDto.builder()
                .restaurant(review.getRestaurant())
                .user(review.getUser())
                .subject(review.getSubject())
                .content(review.getContent())
                .rate(review.getRate())
                .build();
    }
}
