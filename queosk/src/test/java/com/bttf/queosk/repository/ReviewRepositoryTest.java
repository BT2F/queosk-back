package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.model.UserStatus;
import com.bttf.queosk.entity.RestaurantEntity;
import com.bttf.queosk.entity.ReviewEntity;
import com.bttf.queosk.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;

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
        UserEntity user = UserEntity.builder()
                .id(1L)
                .userId("test")
                .email("a@x.com")
                .password("test")
                .phone("0100000000")
                .status(UserStatus.NOT_VERIFIED)
                .build();

        userRepository.save(user);

        RestaurantEntity restaurant = RestaurantEntity.builder()
                .id(1L)
                .ownerId("test")
                .name("test")
                .password("asd")
                .build();

        restaurantRepository.save(restaurant);

        ReviewEntity review = ReviewEntity.builder()
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