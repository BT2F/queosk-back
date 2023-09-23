package com.bttf.queosk.entity;

import com.bttf.queosk.dto.QueueCreationRequest;
import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

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

    @Column
    private Long userId;

    @Column
    private Long numberOfParty;

    @Column
    private Long restaurantId;

    @Column
    private boolean isDone;

    public static Queue of(QueueCreationRequest queueCreateRequest, Long restaurantId, Long userId) {
        return Queue.builder()
                .numberOfParty(queueCreateRequest.getNumberOfParty())
                .restaurantId(restaurantId)
                .userId(userId)
                .build();
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }
}