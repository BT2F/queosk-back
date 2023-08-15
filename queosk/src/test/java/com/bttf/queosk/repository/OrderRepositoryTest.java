package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.entity.MenuEntity;
import com.bttf.queosk.entity.OrderEntity;
import com.bttf.queosk.entity.RestaurantEntity;
import com.bttf.queosk.entity.TableEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.bttf.queosk.model.TableStatus.OPEN;
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

        RestaurantEntity restaurant = restaurantRepository.save(RestaurantEntity
                .builder()
                .id(1L)
                .build());

        TableEntity table = TableEntity.builder()
                .id(1L)
                .status(OPEN)
                .restaurant(restaurant)
                .build();


        MenuEntity menu = MenuEntity.builder()
                .id(1L)
                .name("test Menu")
                .price(20000L)
                .restaurant(restaurant)
                .build();

        tableRepository.save(table);

        menuRepository.save(menu);

        OrderEntity order = OrderEntity.builder()
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