package com.bttf.queosk.dto.queuedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueResponseForRestaurant {
    private Integer totalQueue;
    private List<QueueDto> queueDtoList;
}
