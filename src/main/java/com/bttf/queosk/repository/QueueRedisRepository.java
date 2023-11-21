package com.bttf.queosk.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class QueueRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public String createQueue(String restaurantId, String queueId) {
        redisTemplate.opsForList().rightPush(restaurantId, queueId);
        redisTemplate.expire(restaurantId, 24, TimeUnit.HOURS);
        return queueId;
    }

    public List<String> findAll(String restaurantId) {
        return redisTemplate.opsForList().range(restaurantId, 0, -1);
    }

    public Long getUserWaitingCount(String restaurantId, String queueId) {
        return redisTemplate.opsForList().indexOf(restaurantId, queueId);
    }

    public String popTheFirstTeamOfQueue(String restaurantId) {
        return redisTemplate.opsForList().leftPop(restaurantId);
    }

    public void deleteQueue(String restaurantId, String queueId) {
        redisTemplate.opsForList().remove(restaurantId, 0, queueId);
    }

    public boolean setIfNotExists(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value));
    }

    public void deleteLockKey(String key) {
        redisTemplate.delete(key);
    }

    public void expire(String key, int expireSeconds) {
        redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
    }
}