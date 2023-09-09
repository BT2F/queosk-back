package com.bttf.queosk.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueueRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void createQueue(String restaurantId, String queueId) {
        redisTemplate.opsForList().rightPush(restaurantId, queueId);
    }

    public List<String> findAll(String restaurantId) {
        return redisTemplate.opsForList().range(restaurantId, 0, -1);
    }

    public Long getUserWaitingCount(String restaurantId, String queueId) {
        return redisTemplate.opsForList().indexOf(restaurantId, queueId);
    }

    public void popTheFirstTeamOfQueue(String restaurantId) {
        redisTemplate.opsForList().leftPop(restaurantId);
    }

    public void deleteQueue(String restaurantId, String queueId) {
        redisTemplate.opsForList().remove(restaurantId, 0, queueId);
    }
}
