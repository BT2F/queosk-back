package com.bttf.queosk.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class QueueRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public Long createQueue(String restaurantId, String queueId) {
        redisTemplate.opsForZSet().add(restaurantId, queueId, System.currentTimeMillis());
        return redisTemplate.opsForZSet().size(restaurantId);
    }

    public Set<?> findAll(String restaurantId) {
//        redisTemplate.opsForZSet().rank(restaurantId,3)
        return redisTemplate.opsForZSet().range(restaurantId, 0, -1);

    }

    public Long getUserWaitingCount(String restaurantId, String queueId) {
        return redisTemplate.opsForZSet().rank(restaurantId, queueId);
    }

    public void deleteWaitingTeam(String restaurantId) {
        ZSetOperations.TypedTuple<String> stringTypedTuple = redisTemplate.opsForZSet()
                .popMin(restaurantId);
    }
}
