package com.bttf.queosk.service;

import com.bttf.queosk.dto.MenuItemDto;
import com.bttf.queosk.dto.SettlementDto;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Settlement;
import com.bttf.queosk.repository.MenuItemRepository;
import com.bttf.queosk.repository.OrderRepository;
import com.bttf.queosk.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;

    private final OrderRepository orderRepository;

    private final MenuItemRepository menuItemRepository;

    public SettlementDto SettlementGet(Long restaurantId, LocalDateTime from, LocalDateTime to) {
        List<Order> orderByRestaurantInDateRange = orderRepository
                .findOrderByRestaurantInDateRange(restaurantId, from, to);

        //메뉴 별 가격 반환을 위한 Value 에 MenuItemDto 사용
        Map<String, MenuItemDto> menuList = orderByRestaurantInDateRange.stream()
                .flatMap(order -> menuItemRepository.findAllByOrderId(order.getId()).stream())
                .collect(HashMap::new,
                        (map, menuItem) -> map.merge(
                                menuItem.getMenu().getName(),
                                new MenuItemDto(menuItem.getMenu().getPrice(), menuItem.getCount()),
                                (existing, replacement) -> existing.addCount(replacement.getCount())
                        ),
                        (map1, map2) -> map2.forEach((key, value) -> map1.merge(
                                key,
                                value,
                                (existing, replacement) -> existing.addCount(replacement.getCount())
                        ))
                );

        List<SettlementDto.OrderdMenu> orderdMenuList = menuList.entrySet().stream()
                .map(entry -> new SettlementDto.OrderdMenu(
                        entry.getKey(), entry.getValue().getMenuPrice(), entry.getValue().getCount())
                )
                .collect(Collectors.toList());

        long total = menuList.values().stream()
                .mapToLong(menuItemDto -> menuItemDto.getCount() * menuItemDto.getMenuPrice())
                .sum();

        return SettlementDto.of(orderdMenuList, total);
    }

    public Long periodSettlementPriceGet(Long restaurantId,
                                         LocalDateTime from,
                                         LocalDateTime to) {

        List<Settlement> settlementList =
                settlementRepository.findSettlementsByRestaurantInDateRange(restaurantId, from, to);

        return settlementList.stream()
                .mapToLong(Settlement::getPrice)
                .sum();
    }

}
