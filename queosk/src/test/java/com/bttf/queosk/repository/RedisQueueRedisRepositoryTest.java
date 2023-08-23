package com.bttf.queosk.repository;

import com.bttf.queosk.model.usermodel.UserStatus;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
class RedisQueueRedisRepositoryTest {

    @Autowired
    private QueueRedisRepository queueRedisRepository;

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
                .email("a@x.com")
                .password("test")
                .phone("0100000000")
                .status(UserStatus.NOT_VERIFIED)
                .build();

//        Queue queue = Queue.builder()
//                .id(1L)
//                .restaurantId(restaurant)
//                .userId(user)
//                .build();
//        // when
//        queueRedisRepository.save(queue);
//
//        // then
//
//        queueRedisRepository.findById(queue.getId());
//
//        queueRedisRepository.count();
//
//        queueRedisRepository.delete(queue);
    }

}