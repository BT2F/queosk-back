package com.bttf.queosk.dto.queuedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueResponseForUser {
    private Long userQueueIndex;
    private Long queueRemaining;
}
