package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.entity.QueueEntity;
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
        QueueEntity queue = QueueEntity.builder()
                .id("1")
                .restaurantId("1")
                .userId("1")
                .build();
        // when
        queueRepository.save(queue);

        // then

        queueRepository.findById(queue.getId());

        queueRepository.count();

        queueRepository.delete(queue);
    }

}