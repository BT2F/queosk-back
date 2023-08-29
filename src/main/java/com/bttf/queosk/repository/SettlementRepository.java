package com.bttf.queosk.repository;

import com.bttf.queosk.dto.settlementdto.SettlementDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SettlementRepository {
    List<SettlementDto.OrderdMenu> getTodaySales(Long restaurantId);

    List<SettlementDto.OrderdMenu> getPeriodSales(Long restaurantId, LocalDateTime to, LocalDateTime from);
}