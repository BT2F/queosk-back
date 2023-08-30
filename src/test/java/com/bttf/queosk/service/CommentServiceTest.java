package com.bttf.queosk.service;

import com.bttf.queosk.dto.CommentForm;
import com.bttf.queosk.entity.Comment;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.repository.CommentRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.ReviewRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Rollback
@Transactional
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
    void createComment_success() throws Exception {
        // given
        Review review = Review.builder().id(1L).build();
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        CommentForm commentForm = CommentForm.builder().content("test").build();

        // when
        commentService.createComment(1L, 1L, commentForm);

        // then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment() {
        // given
        Review review = Review.builder().id(1L).build();
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        Comment comment = Comment.builder()
                .id(1L)
                .restaurant(restaurant)
                .review(review)
                .isDeleted(false)
                .content("test").build();

        given(commentRepository.findByIdAndIsDeletedFalse(1L)).willReturn(comment);

        CommentForm form = CommentForm.builder().content("test2").build();

        // when

        commentService.updateComment(1L, 1L, form);

        verify(commentRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        assertThat(commentRepository.findByIdAndIsDeletedFalse(1L).getContent()).isEqualTo("test2");
    }

    @Test
    void deleteComment() {
        // given
        Restaurant restaurant = Restaurant.builder().id(1L).build();
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        Comment comment = Comment.builder()
                .id(1L)
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