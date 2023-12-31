package com.bttf.queosk.service;

import com.bttf.queosk.dto.OrderCreationRequestForm;
import com.bttf.queosk.entity.*;
import com.bttf.queosk.enumerate.OperationStatus;
import com.bttf.queosk.enumerate.OrderStatus;
import com.bttf.queosk.enumerate.TableStatus;
import com.bttf.queosk.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.bttf.queosk.enumerate.MenuStatus.ON_SALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
@DisplayName("주문 관련 테스트코드")
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private TableRepository tableRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("주문생성 (성공)")
    public void createOrder_success() {
        // Given
        Long restaurantId = 1L;
        Long tableId = 1L;

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .operationStatus(OperationStatus.OPEN)
                .build();

        Menu menu1 = Menu.builder()
                .id(1L)
                .name("menu1")
                .price(1000L)
                .status(ON_SALE)
                .restaurantId(restaurantId)
                .build();
        Menu menu2 = Menu.builder()
                .id(2L)
                .name("menu2")
                .price(1000L)
                .status(ON_SALE)
                .restaurantId(restaurantId)
                .build();

        Table table = Table.builder()
                .id(1L)
                .status(TableStatus.OPEN)
                .restaurantId(1L)
                .build();

        OrderCreationRequestForm.MenuItems menuItem1 = OrderCreationRequestForm.MenuItems.builder()
                .menu(1L)
                .count(2)
                .build();

        OrderCreationRequestForm.MenuItems menuItem2 = OrderCreationRequestForm.MenuItems.builder()
                .menu(2L)
                .count(1)
                .build();

        List<OrderCreationRequestForm.MenuItems> menuItems = Arrays.asList(menuItem1, menuItem2);

        OrderCreationRequestForm orderCreationForm =
                OrderCreationRequestForm.builder()
                        .menuItems(menuItems)
                        .tableId(tableId)
                        .restaurantId(restaurantId)
                        .build();

        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        given(tableRepository.findById(1L)).willReturn(Optional.of(table));
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu1));
        given(menuRepository.findById(2L)).willReturn(Optional.of(menu2));

        // When
        orderService.createOrder(orderCreationForm, 1L);

        // Then
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("주문상태 변경 (성공)")
    public void updateOrderStatus_success() {
        // Given
        Long restaurantId = 1L;
        Long userId = 1L;
        Long tableId = 1L;

        Restaurant restaurant = Restaurant.builder()
                .id(userId)
                .build();

        Menu menu1 = Menu.builder()
                .id(1L)
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

        // When
        orderService.updateOrderStatus(1L, 1L, OrderStatus.DONE);

        // Then
        verify(orderRepository, times(1)).save(any(Order.class));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DONE);
    }
}
