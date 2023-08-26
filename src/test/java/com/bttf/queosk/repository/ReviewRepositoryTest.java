package com.bttf.queosk.repository;

import com.bttf.queosk.entity.baseentity.JpaAuditingConfiguration;
import com.bttf.queosk.enumerate.UserStatus;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void MenuRepository_test() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("a@x.com")
                .password("test")
                .phone("0100000000")
                .status(UserStatus.NOT_VERIFIED)
                .build();

        userRepository.save(user);

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .ownerId("test")
                .ownerName("test")
                .password("asd")
                .build();

        restaurantRepository.save(restaurant);

        Review review = Review.builder()
                .id(1L)
                .restaurant(restaurant)
                .user(user)
                .content("test")
                .subject("노맛")
                .rate(1.2)
                .build();
        // when

        reviewRepository.save(review);

        // then

        assertThat(reviewRepository.count()).isEqualTo(1);
        assertThat(reviewRepository.findById(review.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 id의 리뷰가 없습니다.")
        ).getSubject()).isEqualTo(review.getSubject());
        assertThat(reviewRepository.findById(review.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 id의 리뷰가 없습니다.")
        ).getContent()).isEqualTo(review.getContent());
        assertThat(reviewRepository.findById(review.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 id의 리뷰가 없습니다.")
        ).getRate()).isEqualTo(review.getRate());


    }

}