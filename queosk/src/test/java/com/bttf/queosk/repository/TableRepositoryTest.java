package com.bttf.queosk.repository;

import com.bttf.queosk.config.JpaAuditingConfiguration;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.Table;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.bttf.queosk.dto.enumerate.TableStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

@Import(JpaAuditingConfiguration.class)
@DataJpaTest
public class TableRepositoryTest {
    @Autowired
    private TableRepository tableRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void setTable_test() throws Exception {
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
        // when
        Table savedTable = tableRepository.save(table);
        // then

        assertThat(tableRepository.count()).isEqualTo(1L);
    }

    @Test
    public void getTable_test() throws Exception {
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
        tableRepository.save(table);
        // when
        Table findTable = tableRepository.findById(table.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 테이블이 없습니다."));
        // then

        assertThat(findTable.getStatus()).isEqualTo(OPEN);
    }


}