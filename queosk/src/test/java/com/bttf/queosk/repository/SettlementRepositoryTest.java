package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static com.bttf.queosk.dto.TableStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
class SettlementRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private SettlementRepository settlementRepository;

    @Test
    public void CalculateRepository_test() throws Exception {
        // given

        Restaurant restaurant = restaurantRepository.save(Restaurant
                .builder()
                .id(1L)
                .build());

        Table table = Table.builder()
                .id(1L)
                .status(OPEN)
                .restaurant(restaurant)
                .build();

        Menu menu = Menu.builder()
                .id(1L)
                .name("test Menu")
                .price(20000L)
                .restaurant(restaurant)
                .build();

        Order order = Order.builder()
                .id(1L)
                .table(table)
                .restaurant(restaurant)
                .menu(menu)
                .count(3L)
                .build();

        tableRepository.save(table);

        menuRepository.save(menu);

        orderRepository.save(order);

        Settlement calculate = Settlement.builder()
                .id(1L)
                .order(order)
                .date(LocalDateTime.now())
                .restaurant(restaurant)
                .build();

        // when

        settlementRepository.save(calculate);

        // then

        assertThat(settlementRepository.existsById(calculate.getId())).isTrue();
        assertThat(settlementRepository.findById(calculate.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 id의 정산 내역이 존재하지 않습니다."))
                .getRestaurant())
                .isEqualTo(restaurant);
    }

}