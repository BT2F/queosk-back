package com.bttf.queosk.repository;

import com.bttf.queosk.dto.SettlementDto;
import com.bttf.queosk.entity.QOrder;
import com.bttf.queosk.enumerate.OrderStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
public class SettlementQueryRepositoryImpl implements SettlementQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<SettlementDto.OrderdMenu> getPeriodSales(Long restaurantId, LocalDateTime to, LocalDateTime from) {
        QOrder order = QOrder.order;

        LocalDateTime startDateTime = LocalDateTime.of(from.toLocalDate(), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime
                .of(to.toLocalDate().plusDays(1), LocalTime.MIN)
                .minusNanos(1);

        NumberPath<Long> menuPriceSum = Expressions.numberPath(Long.class, "menuPriceSum");
        NumberPath<Integer> orderCount = Expressions.numberPath(Integer.class, "orderCount");

        List<SettlementDto.OrderdMenu> orderdMenus =
                jpaQueryFactory
                        .select(
                                Projections.constructor(SettlementDto.OrderdMenu.class,
                                        order.menuItemList.any().menu.name,
                                        orderCount.coalesce(0),
                                        menuPriceSum.coalesce(0L)
                                )
                        )
                        .from(order)
                        .where(order.restaurantId.eq(restaurantId)
                                .and(order.createdAt.between(startDateTime, endDateTime))
                                .and(order.status.eq(OrderStatus.DONE)))
                        .groupBy(order.menuItemList.any().menu.name, order.menuItemList.any().menu.price)
                        .fetch();

        return orderdMenus;
    }


}
