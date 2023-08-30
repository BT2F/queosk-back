package com.bttf.queosk.service;

import com.bttf.queosk.dto.CommentDto;
import com.bttf.queosk.dto.CommentForm;
import com.bttf.queosk.entity.Comment;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.CommentRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void createComment(Long reviewId, Long restaurantId, CommentForm commentForm) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REVIEW));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));
        reviewRestaurantValid(restaurant, review);

        Comment comment = Comment.builder()
                .review(review)
                .restaurant(restaurant)
                .content(commentForm.getContent())
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long commentId, Long restaurantId, CommentForm commentForm) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId);
        Review review = reviewRepository.findById(
                        comment.getReview().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REVIEW));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));
        reviewRestaurantValid(restaurant, review);
        comment.setContent(commentForm.getContent());
    }


    @Transactional
    public void deleteComment(Long commentId, Long restaurantId) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));
        commentRestaurantValid(restaurant, comment);
        comment.delete();
    }


    public List<CommentDto> getComment(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(INVALID_REVIEW));
        return commentRepository
                .findByReviewAndIsDeletedFalse(review)
                .stream()
                .map(CommentDto::of)
                .collect(Collectors.toList());
    }

    private void reviewRestaurantValid(Restaurant restaurant, Review review) {
        if (!restaurant.getId().equals(review.getId())) {
            throw new CustomException(REVIEW_RESTAURANT_NOT_MATCH);
        }
    }

    private void commentRestaurantValid(Restaurant restaurant, Comment comment) {
        if (!restaurant.getId().equals(comment.getRestaurant().getId())) {
            throw new CustomException(COMMENT_RESTAURANT_NOT_MATCH);
        }
    }
}
