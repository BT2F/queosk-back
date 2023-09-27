package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Queue;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(value = "Queue Dto")
public class QueueDto {

    private Long id;

    private Long numberOfParty;

    private Long userId;

    private Long restaurantId;

    public static QueueDto of(Queue queue) {
        return QueueDto.builder()
                .id(queue.getId())
                .userId(queue.getUserId())
                .numberOfParty(queue.getNumberOfParty())
                .restaurantId(queue.getRestaurantId())
                .build();
    }
}
