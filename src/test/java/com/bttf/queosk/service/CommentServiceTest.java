package com.bttf.queosk.service;

import com.bttf.queosk.dto.CommentRequestForm;
import com.bttf.queosk.dto.CommentResponseForm;
import com.bttf.queosk.entity.Comment;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.repository.CommentRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Rollback
@Transactional
@DisplayName("대댓글 관련 테스트코드")
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("대댓글 생성")
    void createComment_success() throws Exception {
        // given
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        Review review = Review.builder().id(1L).commentNum(0).restaurant(restaurant).build();
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        CommentRequestForm commentForm = CommentRequestForm.builder().
                content("test")
                .build();

        // when
        commentService.createComment(1L, 1L, commentForm);

        // then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("대댓글 수정")
    void updateComment() {
        // given
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        Review review = Review.builder().id(1L).restaurant(restaurant).build();
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        Comment comment = Comment.builder()
                .id(1L)
                .restaurant(restaurant)
                .review(review)
                .isDeleted(false)
                .content("test").build();

        given(commentRepository.findByIdAndIsDeletedFalse(1L)).willReturn(comment);

        CommentRequestForm form = CommentRequestForm.builder().content("test2").build();

        // when

        commentService.updateComment(1L, 1L, form);

        verify(commentRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        assertThat(commentRepository.findByIdAndIsDeletedFalse(1L).getContent()).isEqualTo("test2");
    }

    @Test
    @DisplayName("대댓글 삭제")
    void deleteComment() {
        // given
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        Review review = Review.builder().id(1L).commentNum(1)
                .restaurant(restaurant).build();
        given(reviewRepository.findByIdAndIsDeletedFalse(1L)).willReturn(review);
        Comment comment = Comment.builder()
                .id(1L)
                .review(review)
                .restaurant(restaurant)
                .isDeleted(false)
                .content("test").build();

        given(commentRepository.findByIdAndIsDeletedFalse(1L)).willReturn(comment);
        // when
        commentService.deleteComment(1L, 1L);


        verify(commentRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        assertThat(commentRepository.findByIdAndIsDeletedFalse(1L).getIsDeleted()).isEqualTo(true);

    }
}