package com.bttf.queosk.repository;

import com.bttf.queosk.dto.UserStatus;
import com.bttf.queosk.entity.Queue;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class QueueRepositoryTest {

    @Autowired
    private QueueRepository queueRepository;

    @Test
    public void QueueRepository_test() throws Exception {
        // given
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .ownerId("test")
                .ownerName("test")
                .password("asd")
                .build();

        User user = User.builder()
                .id(1L)
                .userId("test")
                .email("a@x.com")
                .password("test")
                .phone("0100000000")
                .status(UserStatus.NOT_VERIFIED)
                .build();

        Queue queue = Queue.builder()
                .id(1L)
                .restaurantId(restaurant)
                .userId(user)
                .build();
        // when
        queueRepository.save(queue);

        // then

        queueRepository.findById(queue.getId());

        queueRepository.count();

        queueRepository.delete(queue);
    }

}