package com.bttf.queosk.service;

import com.bttf.queosk.dto.CreateReviewForm;
import com.bttf.queosk.dto.ReviewDto;
import com.bttf.queosk.dto.UpdateReviewForm;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.ReviewRepository;
import com.bttf.queosk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@Rollback
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @Test
    void createReview_sucess() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));

        CreateReviewForm createReviewForm = CreateReviewForm.builder()
                .restaurantId(1L)
                .subject("test")
                .content("content test")
                .rate(5.0)
                .build();


        // when
        reviewService.createReview(1L, createReviewForm);

        // then

        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(userRepository, times(1)).findById(1L);
        verify(restaurantRepository, times(1)).findById(1L);
    }

    @Test
    void updateReview_success() {

        // given
        User user = User.builder()
                .id(1L)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .build();

        Review review = Review.builder()
                .id(1L)
                .restaurant(restaurant)
                .user(user)
                .subject("test")
                .content("testContent")
                .isDeleted(false)
                .rate(3.2)
                .build();

        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        UpdateReviewForm updateReviewForm = UpdateReviewForm.builder()
                .subject("test1")
                .content("testContent2")
                .rate(4.0)
                .build();

        //when

        reviewService.updateReview(1L, 1L, updateReviewForm);

        // then
        assertThat(reviewRepository.findById(1L).get().getSubject()).isEqualTo("test1");
        assertThat(reviewRepository.findById(1L).get().getContent()).isEqualTo("testContent2");
        assertThat(reviewRepository.findById(1L).get().getRate()).isEqualTo(4.0);


    }

    @Test
    void getReview_success() {
        // given
        Review review = Review.builder()
                .id(1L)
                .isDeleted(false)
                .subject("test")
                .content("doit! now!")
                .build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // when

        ReviewDto reviewDto = reviewService.getReview(1L);

        assertThat(reviewDto.getSubject()).isEqualTo("test");
        assertThat(reviewDto.getContent()).isEqualTo("doit! now!");
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void deleteReview_success() {

        // given

        Review review = Review.builder()
                .id(1L)
                .isDeleted(false)
                .subject("test")
                .content("doit! now!")
                .build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        // when

        reviewService.deleteReview(1L, 1L);

        //then

        assertThat(reviewRepository.findById(1L).get().getIsDeleted()).isTrue();
    }

    @Test
    void getReviewList_success() {

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .build();
        Review review = Review.builder()
                .id(1L)
                .restaurant(restaurant)
                .isDeleted(false)
                .subject("test")
                .content("doit! now!")
                .build();
        Review review2 = Review.builder()
                .id(2L)
                .restaurant(restaurant)
                .isDeleted(true)
                .subject("test1")
                .content("doit! now!!")
                .build();

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        when(reviewRepository.findByRestaurantAndIsDeletedFalse(restaurant)).thenReturn(List.of(review));

        List<ReviewDto> reviewList = reviewService.getReviewList(1L);

        assertThat(reviewList.size()).isEqualTo(1);


        assertThat(reviewList.get(0).getRestaurant()).isEqualTo(restaurant);

    }

    @Test
    void getRestaurantUserReviewList_success() {
        User user = User.builder()
                .id(1L)
                .build();
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .build();
        Review review = Review.builder()
                .id(1L)
                .user(user)
                .restaurant(restaurant)
                .isDeleted(false)
                .subject("test")
                .content("doit! now!")
                .build();
        Review review2 = Review.builder()
                .id(2L)
                .restaurant(restaurant)
                .user(user)
                .isDeleted(false)
                .subject("test1")
                .content("doit! now!!")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reviewRepository.findByRestaurantAndUserAndIsDeletedFalse(restaurant, user)).thenReturn(List.of(review, review2));

        List<ReviewDto> reviewList = reviewService.getRestaurantUserReviewList(1L, 1L);
        assertThat(reviewList.size()).isEqualTo(2);
        assertThat(reviewList.get(0).getUser().getId()).isEqualTo(1L);
        assertThat(reviewList.get(1).getUser().getId()).isEqualTo(1L);

    }
}