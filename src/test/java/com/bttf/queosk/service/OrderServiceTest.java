package com.bttf.queosk.service;

import com.bttf.queosk.dto.OrderCreationForm;
import com.bttf.queosk.entity.*;
import com.bttf.queosk.enumerate.OperationStatus;
import com.bttf.queosk.enumerate.OrderStatus;
import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.repository.*;
import com.google.api.services.storage.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.bttf.queosk.enumerate.MenuStatus.ON_SALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private TableRepository tableRepository;

    @Test
    public void createOrder_success() {
        // given

        Long restaurantId = 1L;
        Long menuId = 1L;
        String userEmail = "a@x.com";
        Long tableId = 1L;

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .operationStatus(OperationStatus.OPEN)
                .build();

        Menu menu1 =
                Menu.builder()
                        .id(1L)
                        .name("menu1")
                        .price(1000L)
                        .status(ON_SALE)
                        .restaurantId(restaurantId)
                        .build();

        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        User user = User.builder()
                .id(1L)
                .email(userEmail)
                .build();

        MenuItem menuItem = MenuItem.builder()
                .menu(menu1)
                .count(1)
                .build();

        List<MenuItem> menuItems = List.of(menuItem);



        OrderCreationForm.Request orderCreationForm = OrderCreationForm.Request.builder()
                .menuItems(menuItems)
                .tableId(tableId)
                .restaurantId(restaurantId)
                .build();

        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(tableRepository.findById(1L)).willReturn(Optional.of(table));
        // when

        orderService.createOrder(orderCreationForm, 1L);

        // then

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void updateOrderStatus_success() {
        // given

        Long restaurantId = 1L;
        Long menuId = 1L;
        Long userId = 1L;
        Long tableId = 1L;

        Restaurant restaurant = Restaurant.builder()
                .id(userId)
                .build();

        Menu menu1 =
                Menu.builder()
                        .id(menuId)
                        .name("menu1")
                        .price(1000L)
                        .status(ON_SALE)
                        .restaurantId(restaurantId)
                        .build();

        Table table = Table.builder()
                .id(tableId)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        User user = User.builder()
                .id(userId)
                .build();

        Order order = Order.builder()
                .userId(user.getId())
                .restaurantId(restaurant.getId())
                .status(OrderStatus.IN_PROGRESS)
                .build();

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        // when

        orderService.updateOrderStatus(1L, 1L, OrderStatus.DONE);

        // then

        verify(orderRepository, times(1)).save(any(Order.class));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DONE);
    }
}