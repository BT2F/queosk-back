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
    private Long restaurantId;
    private String restaurantName;
    private String userNickname;
    private String userImage;
    private String subject;
    private String content;
    private String imageUrl;
    private Double rate;
    private Integer commentNum;

    public static ReviewDto of(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .restaurantId(review.getRestaurant().getId())
                .restaurantName(review.getRestaurant().getRestaurantName())
                .userNickname(review.getUser().getNickName())
                .userImage(review.getUser().getImageUrl())
                .subject(review.getSubject())
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .rate(review.getRate())
                .commentNum(review.getCommentNum())
                .build();
    }
}
