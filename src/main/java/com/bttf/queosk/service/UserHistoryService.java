package com.bttf.queosk.service;

import com.bttf.queosk.dto.UserHistoryDto;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.enumerate.OrderStatus;
import com.bttf.queosk.repository.OrderRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserHistoryService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public List<UserHistoryDto> getUserHistories(Long userId) {
        List<Order> orders = orderRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(
                userId, OrderStatus.IN_PROGRESS
        );

        return orders.stream()
                .map(order -> {
                    String restaurantName = restaurantRepository.findById(order.getRestaurantId())
                            .map(Restaurant::getRestaurantName)
                            .orElse(null);
                    return UserHistoryDto.of(order, restaurantName);
                })
                .collect(Collectors.toList());
    }
}
