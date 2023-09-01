package com.bttf.queosk.repository;

import com.bttf.queosk.dto.SettlementDto;
import com.bttf.queosk.entity.QOrder;
import com.bttf.queosk.enumerate.OrderStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
public class SettlementRepositoryImpl implements SettlementRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<SettlementDto.OrderdMenu> getTodaySales(Long restaurantId) {
        QOrder order = QOrder.order;

        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime
                .of(today.plusDays(1), LocalTime.MIN)
                .minusNanos(1);

        return jpaQueryFactory
                .select(Projections.constructor(SettlementDto.OrderdMenu.class, order.menu.name, order.count, order.menu.price))
                .from(order)
                .where(order.restaurant.id.eq(restaurantId)
                        .and(order.createdAt.between(startDateTime, endDateTime))
                        .and(order.status.eq(OrderStatus.DONE)))
                .groupBy(order.menu)
                .fetch();
    }

    public List<SettlementDto.OrderdMenu> getPeriodSales(Long restaurantId, LocalDateTime to, LocalDateTime from) {
        QOrder order = QOrder.order;

        return jpaQueryFactory
                .select(Projections.constructor(SettlementDto.OrderdMenu.class, order.menu.name, order.count, order.menu.price))
                .from(order)
                .where(order.restaurant.id.eq(restaurantId)
                        .and(order.createdAt.between(from, to))
                        .and(order.status.eq(OrderStatus.DONE)))
                .groupBy(order.menu)
                .fetch();
    }
}
