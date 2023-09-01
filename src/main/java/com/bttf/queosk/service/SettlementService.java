package com.bttf.queosk.service;

import com.bttf.queosk.dto.SettlementDto;
import com.bttf.queosk.repository.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final OrderQueryRepository queryRepository;

    public SettlementDto todaySettlementGet(Long restaurantId) {

        List<SettlementDto.OrderdMenu> settlement = queryRepository.getTodaySales(restaurantId);

        Long sum = settlement.stream().mapToLong(SettlementDto.OrderdMenu::sumOfPrice).sum();


        return SettlementDto.of(settlement, sum);
    }

    public SettlementDto periodSettlementGet(Long restaurantId,
                                             LocalDateTime to,
                                             LocalDateTime from) {

        List<SettlementDto.OrderdMenu> settlement = queryRepository.getPeriodSales(restaurantId, to, from);

        Long sum = settlement.stream().mapToLong(SettlementDto.OrderdMenu::sumOfPrice).sum();


        return SettlementDto.of(settlement, sum);
    }
}
