package com.bttf.queosk.entity;

import com.bttf.queosk.entity.baseentity.BaseTimeEntity;
import com.bttf.queosk.enumerate.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Entity(name = "order")
@AuditOverride(forClass = BaseTimeEntity.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long tableId;

    @Column
    private Long userId;

    @Column
    private Long restaurantId;

    @Column
    private Long menuId;

    @Column
    private Integer count;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public void setStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }
}
