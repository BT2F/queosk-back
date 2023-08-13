package com.bttf.queosk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "queue")
public class QueueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private RestaurantEntity restaurantId;

    private UserEntity userId;
}
