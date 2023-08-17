package com.bttf.queosk.entity;

import com.bttf.queosk.config.baseEntity.BaseTimeEntity;
import com.bttf.queosk.dto.enumerate.OrderStatus;
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

    @ManyToOne
    @JoinColumn(name = "table_id")
    private Table table;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private Long count;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
