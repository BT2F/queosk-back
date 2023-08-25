package com.bttf.queosk.entity;

import com.bttf.queosk.dto.queuedto.QueueDto;
import lombok.*;
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
