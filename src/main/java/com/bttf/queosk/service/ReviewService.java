package com.bttf.queosk.service;

import com.bttf.queosk.dto.CreateReviewForm;
import com.bttf.queosk.dto.ReviewDto;
import com.bttf.queosk.dto.UpdateReviewForm;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.ReviewRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;


    @Transactional
    public void createReview(Long userId, CreateReviewForm createReviewForm) {
        User user = getUser(userId);

        Restaurant restaurant = getRestaurant(createReviewForm.getRestaurantId());

        Review review = Review.builder()
                .restaurant(restaurant)
                .user(user)
                .subject(createReviewForm.getSubject())
                .content(createReviewForm.getContent())
                .rate(createReviewForm.getRate())
                .build();

        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(Long reviewId, Long userId, UpdateReviewForm updateReviewForm) {
        Review review = findReview(reviewId);
        validReviewUser(userId, review);
        review.setReview(updateReviewForm.getSubject(), updateReviewForm.getContent(), updateReviewForm.getRate());
    }

    public ReviewDto getReview(Long reviewId) {
        Review review = findReview(reviewId);
        return ReviewDto.of(review);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = findReview(reviewId);
        validReviewUser(userId, review);
        review.delete();
    }

    public List<ReviewDto> getReviewList(Long restaurantId) {
        List<Review> reviewList = reviewRepository.findByRestaurantAndIsDeletedFalse(getRestaurant(restaurantId));
        return reviewList.stream().map(ReviewDto::of).collect(Collectors.toList());
    }

    public List<ReviewDto> getRestaurantUserReviewList(Long userId, Long restaurantId) {
        List<Review> reviewList = reviewRepository.findByRestaurantAndUserAndIsDeletedFalse(
                getRestaurant(restaurantId), getUser(userId));
        return reviewList.stream().map(ReviewDto::of).collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_EXISTS));
    }

    private Restaurant getRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new CustomException(INVALID_RESTAURANT));
    }

    private Review findReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new CustomException(INVALID_REVIEW));

        if (review.getIsDeleted()) {
            throw new CustomException(REVIEW_IS_DELETED);
        }

        return review;
    }

    private void validReviewUser(Long userId, Review review) {
        if (!Objects.equals(review.getId(), userId)) {
            throw new CustomException(REVIEW_WRITER_NOT_MATCH);
        }
    }

}
