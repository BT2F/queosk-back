package com.bttf.queosk.service;

import com.bttf.queosk.dto.orderdto.CreateOrderForm;
import com.bttf.queosk.dto.orderdto.OrderDto;
import com.bttf.queosk.entity.*;
import com.bttf.queosk.enumerate.OrderStatus;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.bttf.queosk.enumerate.MenuStatus.*;
import static com.bttf.queosk.enumerate.OperationStatus.*;
import static com.bttf.queosk.enumerate.OrderStatus.*;
import static com.bttf.queosk.enumerate.TableStatus.*;
import static com.bttf.queosk.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createOrder(CreateOrderForm createOrderForm, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_EXISTS));
        Restaurant restaurant = restaurantRepository.findById(createOrderForm.getRestaurantId()).orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));
        Table table = tableRepository.findById(createOrderForm.getTableId()).orElseThrow(() -> new CustomException(ErrorCode.INVALID_TABLE));
        Menu menu = menuRepository.findByIdAndRestaurantId(createOrderForm.getMenuId(), restaurant.getId()).orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        validOrder(restaurant, table, menu);

        Order order = Order.builder().restaurant(restaurant).table(table).menu(menu).user(user).count(createOrderForm.getCount()).status(IN_PROGRESS).build();

        orderRepository.save(order);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Long restaurantId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
        orderRestaurantValidation(order, restaurantId);

        order.setStatus(orderStatus);

        orderRepository.save(order);
    }


    public OrderDto readOrder(Long orderId, Long restaurantId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new CustomException(ORDER_NOT_FOUND));
        orderRestaurantValidation(order, restaurantId);
        return OrderDto.of(order);
    }

    public List<OrderDto> readTodayOrderList(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new CustomException(INVALID_RESTAURANT));
        LocalDateTime startTime = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        List<Order> orderList =
                orderRepository.findByRestaurantAndCreatedAtBetween(restaurant, startTime, endTime);
        return orderToOrderDto(orderList);
    }

    public List<OrderDto> readInProgressOrderList(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new CustomException(INVALID_RESTAURANT));
        List<Order> orderList = orderRepository.findAllByRestaurantAndStatus(IN_PROGRESS, restaurant);
        return orderToOrderDto(orderList);
    }

    private List<OrderDto> orderToOrderDto(List<Order> orderList){
        List<OrderDto> orderDtoList = new ArrayList<>();

        orderList.forEach(order -> {
            orderDtoList.add(OrderDto.of(order));
        });

        return orderDtoList;
    }

    private void validOrder(Restaurant restaurant, Table table, Menu menu) {
        if (restaurant.getOperationStatus() == CLOSED) {
            throw new CustomException(RESTAURANT_CLOSED);
        }
        if (menu.getStatus() == SOLD_OUT) {
            throw new CustomException(MENU_SOLD_OUT);
        }
        if (table.getStatus() == USING) {
            throw new CustomException(TABLE_IS_USING);
        }
    }

    public void orderRestaurantValidation(Order order, Long userId) {
        if (!order.getRestaurant().getId().equals(userId)) {
            throw new CustomException(ORDER_RESTAURANT_NOT_MATCH);
        }
    }


}
