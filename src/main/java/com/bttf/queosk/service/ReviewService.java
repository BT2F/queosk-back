package com.bttf.queosk.service;

import com.bttf.queosk.dto.CreateReviewForm;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.ReviewRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.bttf.queosk.exception.ErrorCode.INVALID_RESTAURANT;
import static com.bttf.queosk.exception.ErrorCode.USER_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public void createReview(Long userId, CreateReviewForm createReviewForm) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_EXISTS)
        );

        Restaurant restaurant = restaurantRepository.findById(createReviewForm.getRestaurantId()).orElseThrow(
                () -> new CustomException(INVALID_RESTAURANT)
        );

        Review review = Review.builder()
                .restaurant(restaurant)
                .user(user)
                .subject(createReviewForm.getSubject())
                .content(createReviewForm.getContent())
                .rate(createReviewForm.getRate())
                .build();

        reviewRepository.save(review);
    }
}
