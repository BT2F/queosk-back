package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Review;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Review Dto")
public class ReviewDto {
    private Long id;
    private RestaurantDto restaurant;
    private UserDto user;
    private String subject;
    private String content;
    private String imageUrl;
    private Double rate;
    private Integer commentNum;

    public static ReviewDto of(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .restaurant(RestaurantDto.of(review.getRestaurant()))
                .user(UserDto.of(review.getUser()))
                .subject(review.getSubject())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .rate(review.getRate())
                .commentNum(review.getCommentNum())
                .build();
    }
}
