package com.bttf.queosk.service;

import com.bttf.queosk.dto.OrderCreationRequestForm;
import com.bttf.queosk.dto.OrderDto;
import com.bttf.queosk.entity.MenuItem;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Table;
import com.bttf.queosk.enumerate.OrderStatus;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.bttf.queosk.enumerate.MenuStatus.SOLD_OUT;
import static com.bttf.queosk.enumerate.OperationStatus.CLOSED;
import static com.bttf.queosk.enumerate.OrderStatus.DONE;
import static com.bttf.queosk.enumerate.OrderStatus.IN_PROGRESS;
import static com.bttf.queosk.enumerate.TableStatus.USING;
import static com.bttf.queosk.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final TableRepository tableRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional
    public void createOrder(OrderCreationRequestForm orderCreationRequest, Long userId) {

        Restaurant restaurant = getRestaurant(orderCreationRequest.getRestaurantId());

        Table table = getTable(orderCreationRequest.getTableId());

        Order order = Order.builder()
                .restaurantId(orderCreationRequest.getRestaurantId())
                .tableId(table.getId())
                .userId(userId)
                .status(IN_PROGRESS)
                .build();

        List<MenuItem> menuItemList = orderCreationRequest.getMenuItems().stream().map(menuItems -> MenuItem.builder()
                .menu(menuRepository.findById(menuItems.getMenu()).orElseThrow(() -> new CustomException(MENU_NOT_FOUND)))
                .count(menuItems.getCount())
                .order(order)
                .build()).collect(Collectors.toList());

        validOrder(restaurant, table, menuItemList);
        table.setStatus(USING);
        orderRepository.save(order);
        menuItemRepository.saveAll(menuItemList);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Long restaurantId, OrderStatus orderStatus) {
        Order order = getOrder(orderId);
        orderRestaurantValidation(order, restaurantId);

        order.setStatus(orderStatus);

        orderRepository.save(order);
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }


    public OrderDto readOrder(Long orderId, Long restaurantId) {
        Order order = getOrder(orderId);
        List<MenuItem> menuItems = menuItemRepository.findAllByOrderId(orderId);
        orderRestaurantValidation(order, restaurantId);
        return OrderDto.of(order, menuItems);
    }

    public List<OrderDto> readTodayOrderList(Long restaurantId) {
        Restaurant restaurant = getRestaurant(restaurantId);
        LocalDateTime startTime = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

        List<Order> orderList = orderRepository.findByRestaurantIdAndCreatedAtBetween(
                restaurant.getId(), startTime, endTime
        );

        return orderToOrderDto(orderList);
    }

    public List<OrderDto> readInProgressOrderList(Long restaurantId) {
        List<Order> orderList =
                orderRepository.findAllByRestaurantIdAndStatus(restaurantId, IN_PROGRESS);

        return orderToOrderDto(orderList);
    }

    private List<OrderDto> orderToOrderDto(List<Order> orderList) {

        return orderList.stream()
                .map(order -> OrderDto.of(order, menuItemRepository.findAllByOrderId(order.getId())))
                .collect(Collectors.toList());
    }

    public List<OrderDto> readItodayDoneList(Long restaurantId) {

        LocalDateTime startTime = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endTime = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

        List<Order> orderList =
                orderRepository.findByRestaurantIdAndCreatedAtBetweenAndStatus(restaurantId, startTime, endTime, DONE);

        return orderToOrderDto(orderList);
    }

    private Table getTable(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TABLE));
    }

    private Restaurant getRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_RESTAURANT));
    }

    private void validOrder(Restaurant restaurant, Table table, List<MenuItem> menuItems) {
        if (restaurant.getOperationStatus().equals(CLOSED)) {
            throw new CustomException(RESTAURANT_CLOSED);
        }
        menuItems.forEach(menuItem -> {
                    if (menuItem.getMenu().getStatus().equals(SOLD_OUT)) {
                        throw new CustomException(MENU_SOLD_OUT);
                    }
                }
        );

        if (table.getStatus().equals(USING)) {
            throw new CustomException(TABLE_IS_USING);
        }
    }

    public void orderRestaurantValidation(Order order, Long restaurantId) {
        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new CustomException(ORDER_RESTAURANT_NOT_MATCH);
        }
    }
}
