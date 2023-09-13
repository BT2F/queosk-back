package com.bttf.queosk.repository;

import com.bttf.queosk.dto.SettlementDto;
import com.bttf.queosk.entity.QMenu;
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
public class SettlementQueryRepositoryImpl implements SettlementQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<SettlementDto.OrderdMenu> getTodaySales(Long restaurantId) {
        QOrder order = QOrder.order;
        QMenu menu = QMenu.menu;

        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime
                .of(today.plusDays(1), LocalTime.MIN)
                .minusNanos(1);

        return getOrderdMenus(restaurantId, order, menu, startDateTime, endDateTime);
    }



    public List<SettlementDto.OrderdMenu> getPeriodSales(Long restaurantId, LocalDateTime to, LocalDateTime from) {
        QOrder order = QOrder.order;
        QMenu menu = QMenu.menu; // 메뉴 엔티티의 QueryDSL Q클래스를 가져오기

        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.from(from), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime
                .of(LocalDate.from(to.plusDays(1)), LocalTime.MIN)
                .minusNanos(1);

        return getOrderdMenus(restaurantId, order, menu, startDateTime, endDateTime);
    }

    private List<SettlementDto.OrderdMenu> getOrderdMenus(Long restaurantId, QOrder order, QMenu menu,
                                                          LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                SettlementDto.OrderdMenu.class,
                                menu.name,
                                order.count.sum(),
                                menu.price
                        )
                )
                .from(order)
                .innerJoin(menu).on(order.menuId.eq(menu.id))
                .where(order.restaurantId.eq(restaurantId)
                        .and(order.createdAt.between(startDateTime, endDateTime))
                        .and(order.status.eq(OrderStatus.DONE)))
                .groupBy(menu.name)
                .fetch();
    }

}
