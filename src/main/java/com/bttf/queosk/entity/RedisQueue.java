package com.bttf.queosk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@RedisHash(value = "restaurantId", timeToLive = 86400)
public class RedisQueue {

    @Id
    private String id;

    private String queueId;
}
