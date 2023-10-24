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
@ApiModel(value = "리뷰 Response")
public class ReviewResponseForm {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private String userNickname;
    private String userImage;
    private String subject;
    private String content;
    private Double rate;

    public static ReviewResponseForm of(ReviewDto reviewDto) {
        return ReviewResponseForm.builder()
                .id(reviewDto.getId())
                .restaurantId(reviewDto.getRestaurantId())
                .restaurantName(reviewDto.getRestaurantName())
                .userNickname(reviewDto.getUserNickname())
                .userImage(reviewDto.getUserImage())
                .subject(reviewDto.getSubject())
                .content(reviewDto.getContent())
                .rate(reviewDto.getRate())
                .build();
    }
}

