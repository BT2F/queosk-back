package com.bttf.queosk.entity;

import com.bttf.queosk.config.baseEntity.BaseTimeEntity;
import com.bttf.queosk.dto.enumerate.TableStatus;
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
public class Table extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TableStatus status;

    private Long restaurantId;

    public static Table of(Long restaurantId) {
        return Table.builder()
                .status(TableStatus.OPEN)
                .restaurantId(restaurantId)
                .build();
    }
}
