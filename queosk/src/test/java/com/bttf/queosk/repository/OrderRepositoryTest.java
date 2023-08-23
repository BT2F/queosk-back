package com.bttf.queosk.repository;

import com.bttf.queosk.entity.baseentity.JpaAuditingConfiguration;
import com.bttf.queosk.entity.Menu;
import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Table;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.bttf.queosk.enumerate.TableStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    public void makeOrder_test() throws Exception {
        // given

        Restaurant restaurant = restaurantRepository.save(Restaurant
                .builder()
                .id(1L)
                .build());

        Table table = Table.builder()
                .id(1L)
                .status(OPEN)
                .restaurantId(restaurant.getId())
                .build();


        Menu menu = Menu.builder()
                .id(1L)
                .name("test Menu")
                .price(20000L)
                .restaurantId(1L)
                .build();

        tableRepository.save(table);

        menuRepository.save(menu);

        Order order = Order.builder()
                .id(1L)
                .table(table)
                .restaurant(restaurant)
                .menu(menu)
                .count(3L)
                .build();

        // when

        orderRepository.save(order);

        // then

        assertThat(orderRepository
                .findById(order.getId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."))
                .getRestaurant()).isEqualTo(restaurant);
    }

}