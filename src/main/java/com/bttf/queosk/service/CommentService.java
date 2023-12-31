package com.bttf.queosk.service;

import com.bttf.queosk.dto.CommentDto;
import com.bttf.queosk.dto.CommentRequestForm;
import com.bttf.queosk.dto.CommentResponseForm;
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
    public void createComment(Long reviewId, Long restaurantId, CommentRequestForm commentRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REVIEW));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));

        reviewRestaurantValid(restaurant, review);

        Comment comment = Comment.builder()
                .review(review)
                .restaurant(restaurant)
                .content(commentRequest.getContent())
                .isDeleted(false)
                .build();

        review.addComment();

        commentRepository.save(comment);
        reviewRepository.save(review);
    }

    @Transactional
    public void updateComment(Long commentId, Long restaurantId, CommentRequestForm commentForm) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId);
        Review review = reviewRepository.findById(comment.getReview().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REVIEW));

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));

        reviewRestaurantValid(restaurant, review);

        comment.setContent(commentForm.getContent());
    }


    @Transactional
    public void deleteComment(Long commentId, Long restaurantId) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(commentId);

        Review review = reviewRepository.findByIdAndIsDeletedFalse(comment.getReview().getId());

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));

        commentRestaurantValid(restaurant, comment);

        reviewCommentExistCheck(review);

        review.deleteComment();

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

    private static void reviewCommentExistCheck(Review review) {
        if (review.getCommentNum() <= 0) {
            throw new CustomException(REVIEW_COMMENT_ZERO);
        }
    }

    private void reviewRestaurantValid(Restaurant restaurant, Review review) {
        if (!restaurant.getId().equals(review.getRestaurant().getId())) {
            throw new CustomException(REVIEW_RESTAURANT_NOT_MATCH);
        }
    }

    private void commentRestaurantValid(Restaurant restaurant, Comment comment) {
        if (!restaurant.getId().equals(comment.getRestaurant().getId())) {
            throw new CustomException(COMMENT_RESTAURANT_NOT_MATCH);
        }
    }
}
