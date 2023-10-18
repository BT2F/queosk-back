package com.bttf.queosk.entity;

import com.bttf.queosk.dto.TableRequestForm;
import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import com.bttf.queosk.enumerate.TableStatus;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

import static com.bttf.queosk.enumerate.TableStatus.OPEN;
import static javax.persistence.EnumType.STRING;

@Entity(name = "table")
@AuditOverride(forClass = BaseTimeEntity.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Table extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(STRING)
    private TableStatus status;

    private Long restaurantId;

    public static Table of(Long restaurantId, TableRequestForm form) {
        return Table.builder()
                .name(form.getTableName())
                .restaurantId(restaurantId)
                .status(OPEN)
                .build();
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public void setName(String name) { this.name = name; }
}
