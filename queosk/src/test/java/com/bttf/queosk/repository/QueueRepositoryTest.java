package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.domain.enumerate.UserStatus;
import com.bttf.queosk.entity.QueueEntity;
import com.bttf.queosk.entity.RestaurantEntity;
import com.bttf.queosk.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
class QueueRepositoryTest {

    @Autowired
    private QueueRepository queueRepository;

    @Test
    public void QueueRepository_test() throws Exception {
        // given
        RestaurantEntity restaurant = RestaurantEntity.builder()
                .id(1L)
                .ownerId("test")
                .name("test")
                .password("asd")
                .build();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .userId("test")
                .email("a@x.com")
                .password("test")
                .phone("0100000000")
                .status(UserStatus.NOT_VERIFIED)
                .build();

        QueueEntity queue = QueueEntity.builder()
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