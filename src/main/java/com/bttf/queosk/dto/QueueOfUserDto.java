package com.bttf.queosk.dto;

import com.bttf.queosk.entity.Queue;
import com.bttf.queosk.entity.Restaurant;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "사용자의 전체 웨이팅 조회 Dto")
public class QueueOfUserDto {
    private Long id;
    private RestaurantDto restaurantDto;
    private Long userQueueIndex;

    public static QueueOfUserDto of(Queue queue,
                                    Restaurant restaurant,
                                    Long userWaitingCount) {
        return QueueOfUserDto.builder()
                .id(queue.getId())
                .restaurantDto(RestaurantDto.of(restaurant))
                .userQueueIndex(userWaitingCount + 1)
                .build();
    }
}