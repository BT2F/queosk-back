package com.bttf.queosk.service;

import com.bttf.queosk.dto.SettlementDto;
import com.bttf.queosk.entity.MenuItem;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Settlement;
import com.bttf.queosk.repository.MenuItemRepository;
import com.bttf.queosk.repository.OrderRepository;
import com.bttf.queosk.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;

    private final OrderRepository orderRepository;

    private final MenuItemRepository menuItemRepository;

    public SettlementDto SettlementGet(Long restaurantId,
                                       LocalDateTime from,
                                       LocalDateTime to) {

        Map<String, Integer> menuList = new HashMap<>();
        Long total = 0L;

        List<Order> orderByRestaurantInDateRange = orderRepository
                .findOrderByRestaurantInDateRange(restaurantId, from, to);

        for (Order order : orderByRestaurantInDateRange) {
            List<MenuItem> allByOrderId = menuItemRepository.findAllByOrderId(order.getId());
            for (MenuItem menuItem : allByOrderId) {
                menuList.put(menuItem.getMenu().getName(), menuList.getOrDefault(menuItem.getMenu().getName(), 0) + menuItem.getCount());
                total += menuItem.getCount() * menuItem.getMenu().getPrice();
            }
        }

        List<SettlementDto.OrderdMenu> orderdMenuList = new ArrayList<>();
        for (Map.Entry<String, Integer> menu : menuList.entrySet()) {
            SettlementDto.OrderdMenu orderdMenu = new SettlementDto.OrderdMenu(menu.getKey(), menu.getValue());
            orderdMenuList.add(orderdMenu);
        }

        return SettlementDto.of(orderdMenuList, total);

    }

    public Long periodSettlementPriceGet(Long restaurantId,
                                         LocalDateTime to,
                                         LocalDateTime from) {
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.from(from), LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime
                .of(LocalDate.from(to.plusDays(1)), LocalTime.MIN)
                .minusNanos(1);
        List<Settlement> settlementList =
                settlementRepository.findSettlementsByRestaurantInDateRange(restaurantId, startDateTime, endDateTime);

        long totalPrice = settlementList.stream()
                .mapToLong(Settlement::getPrice)
                .sum();

        return totalPrice;
    }

}
