package com.bttf.queosk.service;

import com.bttf.queosk.dto.UserHistoryDto;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.MenuItem;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.repository.MenuItemRepository;
import com.bttf.queosk.repository.OrderRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.bttf.queosk.enumerate.MenuStatus.ON_SALE;
import static com.bttf.queosk.enumerate.OrderStatus.DONE;
import static com.bttf.queosk.enumerate.OrderStatus.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Transactional
@DisplayName("UserHistory 관련 테스트코드")
public class UserHistoryTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private MenuItemRepository menuItemRepositoryRepository;

    private UserHistoryService userHistoryService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userHistoryService =
                new UserHistoryService(
                        orderRepository, restaurantRepository,menuItemRepositoryRepository
                );
    }

    @Test
    @DisplayName("사용자 히스토리 조회 (성공)")
    void testCreateUser_Success() {
        //given
        Long userId = 1L;

        Restaurant restaurant = Restaurant.builder()
                .restaurantName("식당식당")
                .id(2L)
                .build();


        Menu menu1 = Menu.builder()
                .imageUrl("aa")
                .name("강북식당")
                .price(1234L)
                .status(ON_SALE)
                .id(2L)
                .restaurantId(2L)
                .build();

        List<MenuItem> menus = new ArrayList<>();

        MenuItem menuItem = MenuItem.builder()
                .menu(menu1)
                .count(1)
                .id(4L)
                .build();

        menus.add(menuItem);

        Order order = Order.builder()
                .id(1L)
                .userId(userId)
                .restaurantId(restaurant.getId())
                .status(DONE)
                .build();

        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(userId, IN_PROGRESS))
                .thenReturn(orders);
        when(restaurantRepository.findById(restaurant.getId()))
                .thenReturn(Optional.of(restaurant));
        when(menuItemRepositoryRepository.findAllByOrderId(1L))
                .thenReturn(Collections.singletonList(menuItem));
        //when

        List<UserHistoryDto> userHistories = userHistoryService.getUserHistories(userId);

        //then
        assertThat(userHistories.get(0).getRestaurantId()).isEqualTo(restaurant.getId());
        assertThat(userHistories.get(0).getRestaurantName()).isEqualTo("식당식당");
        assertThat(userHistories.get(0).getMenuItems()).isEqualTo(menus);
    }

}
