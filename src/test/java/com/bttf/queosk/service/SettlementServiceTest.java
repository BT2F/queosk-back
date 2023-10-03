package com.bttf.queosk.service;

import com.bttf.queosk.dto.SettlementDto;
import com.bttf.queosk.repository.OrderQueryQueryRepository;
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
    private OrderQueryQueryRepository queryRepository;

    @Spy
    @InjectMocks
    private com.bttf.queosk.service.SettlementService settlementService;

    @Test
    @DisplayName("금일 정산 (성공)")
    void testTodaySettlementGet_success() {
        //given
        Long restaurantId = 1L;
        List<SettlementDto.OrderdMenu> list = Arrays.asList(
                new SettlementDto.OrderdMenu("짜장면", 1, 5000L),
                new SettlementDto.OrderdMenu("짬뽕", 2, 7000L),
                new SettlementDto.OrderdMenu("탕수육", 1, 17000L)
        );
        SettlementDto dto = new SettlementDto(list, 36000L);
        LocalDate today = LocalDate.now();
        LocalDateTime from = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime to = LocalDateTime
                .of(today.plusDays(1), LocalTime.MIN)
                .minusNanos(1);

        given(queryRepository.getPeriodSales(restaurantId, to, from)).willReturn(dto.getOrderdMenus());

        //when
        SettlementDto settlementDto = settlementService.SettlementGet(restaurantId, to, from);
        for (SettlementDto.OrderdMenu orderdMenu : list) {
            System.out.println(orderdMenu.getMenu());
            System.out.println(orderdMenu.getTotal());
            System.out.println(orderdMenu.getCount());
        }
        //then
        assertThat(settlementDto.getOrderdMenus()).isEqualTo(list);
        assertThat(settlementDto.getTotal()).isEqualTo(36000L);
        then(settlementService).should(times(1)).SettlementGet(restaurantId, to, from);
    }

    @Test
    @DisplayName("기간 정산 (성공)")
    void testPeriodSettlementGet_success() {
        //given
        Long restaurantId = 1L;
        LocalDate to = LocalDate.now();
        LocalDate from = LocalDate.now().minusDays(3);

        List<SettlementDto.OrderdMenu> list = Arrays.asList(
                new SettlementDto.OrderdMenu("짜장면", 1, 5000L),
                new SettlementDto.OrderdMenu("짬뽕", 2, 7000L),
                new SettlementDto.OrderdMenu("탕수육", 1, 17000L)
        );
        given(queryRepository.getPeriodSales(restaurantId, to.atStartOfDay(), from.atStartOfDay()))
                .willReturn(list);

        //when
        SettlementDto settlementDto = settlementService.SettlementGet(
                restaurantId, to.atStartOfDay(), from.atStartOfDay()
        );

        //then
        assertThat(settlementDto.getOrderdMenus()).isEqualTo(list);
        assertThat(settlementDto.getTotal()).isEqualTo(36000L);
        then(settlementService).should(times(1))
                .SettlementGet(restaurantId, to.atStartOfDay(), from.atStartOfDay());
    }
}