package com.bttf.queosk.entity;

import com.bttf.queosk.config.BaseTimeEntity;
import com.bttf.queosk.model.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Entity(name = "table")
@AuditOverride(forClass = BaseTimeEntity.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TableEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TableStatus status;

    private long restaurantId;

    public static TableEntity of(long restaurantId) {
        return TableEntity.builder()
                .status(TableStatus.OPEN)
                .restaurantId(restaurantId)
                .build();
    }
}
