package com.bttf.queosk.entity;

import com.bttf.queosk.dto.queuedto.QueueDto;
import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@AuditOverride(forClass = BaseTimeEntity.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Queue extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long numberPeople;

    private Long restaurantId;


    public static Queue of(QueueDto queueDto, Long restaurantId, Long userId) {
        return Queue.builder()
                .numberPeople(queueDto.getNumberPeople())
                .restaurantId(restaurantId)
                .userId(userId)
                .build();
    }
}
