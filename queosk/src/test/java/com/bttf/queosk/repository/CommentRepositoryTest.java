package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.entity.Comment;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Review;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.model.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void CommentRepository_test() throws Exception {
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

        reviewRepository.save(review);

        Comment comment = Comment.builder()
                .id(1L)
                .review(review)
                .restaurant(restaurant)
                .content("죄송합니다.")
                .build();

        // when

        commentRepository.save(comment);

        // then

        assertThat(commentRepository.existsById(comment.getId())).isTrue();
        assertThat(commentRepository.findById(comment.getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 id의 코멘트가 없습니다.")
        ).getContent()).isEqualTo(comment.getContent());
    }

}