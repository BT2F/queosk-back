package com.bttf.queosk.service;

import com.bttf.queosk.dto.settlementdto.SettlementDto;
import com.bttf.queosk.repository.OrderQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@Transactional
@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private OrderQueryRepository queryRepository;

    @Spy
    @InjectMocks
    private SettlementService settlementService;

    @Test
    void testTodaySettlementGet_success() {
        //given
        Long restaurantId = 1L;
        List<SettlementDto.OrderdMenu> list = Arrays.asList(
                new SettlementDto.OrderdMenu("짜장면", 1, 5000L),
                new SettlementDto.OrderdMenu("짬뽕", 2, 7000L),
                new SettlementDto.OrderdMenu("탕수육", 1, 17000L)
        );
        given(queryRepository.getTodaySales(restaurantId)).willReturn(list);

        //when
        SettlementDto settlementDto = settlementService.todaySettlementGet(restaurantId);

        //then
        assertThat(settlementDto.getOrderdMenus()).isEqualTo(list);
        assertThat(settlementDto.getPrice()).isEqualTo(36000L);
        then(settlementService).should(times(1)).todaySettlementGet(restaurantId);
    }

    @Test
    void testPeriodSettlementGet_success() {
        //given
        Long restaurantId = 1L;
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = LocalDateTime.now().minusDays(3);

        List<SettlementDto.OrderdMenu> list = Arrays.asList(
                new SettlementDto.OrderdMenu("짜장면", 1, 5000L),
                new SettlementDto.OrderdMenu("짬뽕", 2, 7000L),
                new SettlementDto.OrderdMenu("탕수육", 1, 17000L)
        );
        given(queryRepository.getPeriodSales(restaurantId, to, from)).willReturn(list);

        //when
        SettlementDto settlementDto = settlementService.periodSettlementGet(restaurantId, to, from);

        //then
        assertThat(settlementDto.getOrderdMenus()).isEqualTo(list);
        assertThat(settlementDto.getPrice()).isEqualTo(36000L);
        then(settlementService).should(times(1)).periodSettlementGet(restaurantId, to, from);
    }
}