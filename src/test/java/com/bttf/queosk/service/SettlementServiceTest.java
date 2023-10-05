package com.bttf.queosk.service;

import com.bttf.queosk.dto.SettlementDto;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.MenuItem;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.repository.MenuItemRepository;
import com.bttf.queosk.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("정산 관련 테스트코드")
class SettlementServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuItemRepository menuItemRepository;
    @Spy
    @InjectMocks
    private com.bttf.queosk.service.SettlementService settlementService;

    @Test
    @DisplayName("금일 정산 (성공)")
    void testTodaySettlementGet_success() {

        //given
        Long restaurantId = 1L;
        LocalDate today = LocalDate.now();
        LocalDateTime from = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime to = LocalDateTime
                .of(today.plusDays(1), LocalTime.MIN)
                .minusNanos(1);

        List<Order> orderList = Arrays.asList(
                Order.builder()
                        .restaurantId(restaurantId)
                        .build(),
                Order.builder()
                        .restaurantId(restaurantId)
                        .build(),
                Order.builder()
                        .restaurantId(restaurantId)
                        .build()
        );

        List<MenuItem> menuItemList = Arrays.asList(
                MenuItem.builder()
                        .order(orderList.get(0))
                        .menu(Menu.builder()
                                .name("짜장면")
                                .price(5000L)
                                .build())
                        .count(5)
                        .build(),
                MenuItem.builder()
                        .order(orderList.get(1))
                        .menu(Menu.builder()
                                .name("차돌문어짬뽕")
                                .price(18000L)
                                .build())
                        .count(10)
                        .build(),
                MenuItem.builder()
                        .order(orderList.get(2))
                        .menu(Menu.builder()
                                .name("사천지옥탕수육")
                                .price(36000L)
                                .build())
                        .count(1)
                        .build()
        );

        given(orderRepository.findOrderByRestaurantInDateRange(restaurantId, from, to)).willReturn(orderList);
        for (Order order : orderList) {
            given(menuItemRepository.findAllByOrderId(order.getId())).willReturn(menuItemList);
        }

        //when

        SettlementDto dto = settlementService.SettlementGet(restaurantId, from, to);

        //then
        assertThat(dto.getOrderdMenus().get(0).getMenu()).isEqualTo(menuItemList.get(0).getMenu().getName());
        assertThat(dto.getTotal()).isEqualTo(723000L);
        then(settlementService).should(times(1)).SettlementGet(restaurantId, from, to);
    }

}