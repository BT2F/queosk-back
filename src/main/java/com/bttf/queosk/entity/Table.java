package com.bttf.queosk.entity;

import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import com.bttf.queosk.enumerate.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static com.bttf.queosk.enumerate.TableStatus.OPEN;

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

    public static Table updateStatus(Table table, TableStatus status) {
        return Table.builder()
                .status(status)
                .build();
    }

    public static Table createTableByRestaurantId(Long restaurantId) {
        return Table.builder()
                .restaurantId(restaurantId)
                .status(OPEN)
                .build();
    }
}